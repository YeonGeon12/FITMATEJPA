package kopo.fitmate.dictionary.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import kopo.fitmate.dictionary.client.ApiNinjasClient;
import kopo.fitmate.dictionary.client.YoutubeClient;
import kopo.fitmate.dictionary.dto.ExerciseDTO;
import kopo.fitmate.dictionary.dto.TranslatedExerciseDTO;
import kopo.fitmate.dictionary.dto.YoutubeDTO;
import kopo.fitmate.dictionary.service.IDictionaryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Slf4j
@Service
@RequiredArgsConstructor
public class DictionaryService implements IDictionaryService {

    private final ApiNinjasClient apiNinjasClient;
    private final YoutubeClient youtubeClient;
    private final ObjectMapper objectMapper;

    @Value("${api.papago.id}")
    private String papagoClientId;

    @Value("${api.papago.secret}")
    private String papagoClientSecret;

    @PostConstruct
    void verifyPapagoKeys() {
        if (papagoClientId.isBlank() || papagoClientSecret.isBlank()) {
            throw new IllegalStateException("Papago Client ID/Secret 미설정");
        }
    }


    @Override
    public List<TranslatedExerciseDTO> searchExercises(String name, String muscle) {
        log.info("NCP Papago 기반 운동 목록 검색 시작. 이름: {}, 부위: {}", name, muscle);
        String searchNameInEnglish = name;

        // 1. [최종 수정] 한글 검색어를 GPT 대신 Papago를 이용해 영어로 번역
        if (name != null && !name.isEmpty() && name.matches(".*[ㄱ-ㅎㅏ-ㅣ가-힣]+.*")) {
            try {
                searchNameInEnglish = translateWithPapago(name, "ko", "en");
                log.info("Papago 번역 (한->영): '{}' -> '{}'", name, searchNameInEnglish);
            } catch (Exception e) {
                log.error("Papago 검색어 번역 실패", e);
                return Collections.emptyList();
            }
        }

        List<ExerciseDTO> englishExercises = apiNinjasClient.getExercises(searchNameInEnglish, muscle);
        if (englishExercises == null || englishExercises.isEmpty()) {
            return Collections.emptyList();
        }

        try {
            String textToTranslate = englishExercises.stream().map(ExerciseDTO::getName).collect(Collectors.joining("\n"));
            String translatedText = translateWithPapago(textToTranslate, "en", "ko");
            String[] translatedNames = translatedText.split("\n");

            return IntStream.range(0, englishExercises.size())
                    .mapToObj(i -> TranslatedExerciseDTO.builder()
                            .original(englishExercises.get(i))
                            .translatedName((i < translatedNames.length) ? translatedNames[i].trim() : englishExercises.get(i).getName())
                            .build())
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Papago API 목록 번역 과정에서 오류 발생", e);
            return englishExercises.stream()
                    .map(dto -> TranslatedExerciseDTO.builder().original(dto).translatedName(dto.getName()).build())
                    .collect(Collectors.toList());
        }
    }

    @Override
    public TranslatedExerciseDTO getExerciseDetail(String name) throws Exception {
        log.info("상세 정보 검색 시작 (영어 이름): {}", name);
        List<ExerciseDTO> results = apiNinjasClient.getExercises(name, null);
        if (results == null || results.isEmpty()) return null;

        ExerciseDTO originalExercise = results.get(0);
        String translatedName = translateWithPapago(originalExercise.getName(), "en", "ko");
        String translatedInstructions = translateWithPapago(originalExercise.getInstructions(), "en", "ko");

        return TranslatedExerciseDTO.builder()
                .original(originalExercise)
                .translatedName(translatedName)
                .translatedInstructions(translatedInstructions)
                .build();
    }

    private String translateWithPapago(String text, String sourceLang, String targetLang) throws Exception {
        if (text == null || text.isBlank()) return text;

        // 공식 엔드포인트: /nmt/v1/translation
        // (문서 샘플에 명시)
        final String apiUrl = "https://papago.apigw.ntruss.com/nmt/v1/translation";

        String form = "source=" + URLEncoder.encode(sourceLang, StandardCharsets.UTF_8)
                + "&target=" + URLEncoder.encode(targetLang, StandardCharsets.UTF_8)
                + "&text="   + URLEncoder.encode(text,       StandardCharsets.UTF_8);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(apiUrl))
                .header("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8")
                .header("X-NCP-APIGW-API-KEY-ID", papagoClientId)
                .header("X-NCP-APIGW-API-KEY", papagoClientSecret)
                .POST(HttpRequest.BodyPublishers.ofString(form))
                .build();

        HttpResponse<String> resp = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
        if (resp.statusCode() != 200) {
            log.error("Papago error: status={}, body={}", resp.statusCode(), resp.body());
            return text; // 실패 시 원문 유지(UX 보전)
        }
        JsonNode root = objectMapper.readTree(resp.body());
        return root.path("message").path("result").path("translatedText").asText(text);
    }


    /**
     * 유튜브 영상 검색 및 제목 디코딩
     */
    @Override
    public YoutubeDTO searchYoutube(String query) {
        log.info("YouTube 영상 검색 시작: {}", query);
        String searchQuery = query + " 운동 자세";

        // 1. 평소처럼 YouTube API를 호출합니다.
        YoutubeDTO youtubeDTO = youtubeClient.searchVideos("snippet", searchQuery, "video", 4);

        // 2. [핵심 수정] API 응답 결과가 정상이면, 각 영상의 제목을 디코딩합니다.
        if (youtubeDTO != null && youtubeDTO.getItems() != null) {
            youtubeDTO.getItems().forEach(item -> {
                if (item.getSnippet() != null && item.getSnippet().getTitle() != null) {
                    // HtmlUtils.htmlUnescape를 사용하여 '&#39;' 같은 문자열을 "'"로 변환
                    String decodedTitle = HtmlUtils.htmlUnescape(item.getSnippet().getTitle());
                    item.getSnippet().setTitle(decodedTitle);
                }
            });
        }

        // 3. 디코딩된 제목이 담긴 DTO를 반환합니다.
        return youtubeDTO;
    }
}