// src/main/java/kopo/fitmate/dictionary/service/impl/DictionaryService.java
package kopo.fitmate.dictionary.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import kopo.fitmate.dictionary.client.ApiNinjasClient;
import kopo.fitmate.dictionary.client.YoutubeClient;
import kopo.fitmate.dictionary.dto.ExerciseDTO;
import kopo.fitmate.dictionary.dto.TranslatedExerciseDTO;
import kopo.fitmate.dictionary.dto.YoutubeDTO;
import kopo.fitmate.dictionary.service.IDictionaryService;
import kopo.fitmate.global.config.util.NetworkUtil; // NetworkUtil 임포트
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value; // Value 임포트
import org.springframework.stereotype.Service;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class DictionaryService implements IDictionaryService {

    // 의존성 주입
    private final ApiNinjasClient apiNinjasClient;
    private final YoutubeClient youtubeClient;
    private final ObjectMapper objectMapper; // JSON 파싱을 위해 사용

    // Papago API 키를 application.yaml에서 주입받습니다.
    @Value("${api.papago.id}")
    private String papagoClientId;

    @Value("${api.papago.secret}")
    private String papagoClientSecret;

    /**
     * OpenAI -> Papago API를 사용하여 번역하도록 전체 로직 변경
     */
    @Override
    public List<TranslatedExerciseDTO> searchExercises(String name, String muscle) {
        log.info("운동 정보 목록 검색 시작. 이름: {}, 부위: {}", name, muscle);

        String searchNameInEnglish = name;

        // 한글 검색어가 들어온 경우, 영어로 먼저 번역
        if (name != null && !name.isEmpty() && name.matches(".*[ㄱ-ㅎㅏ-ㅣ가-힣]+.*")) {
            searchNameInEnglish = translateWithPapago(name, "ko", "en");
            log.info("Papago 번역 (한->영): '{}' -> '{}'", name, searchNameInEnglish);
        }

        // API Ninjas로부터 영어 결과 목록을 받음
        List<ExerciseDTO> englishResults = apiNinjasClient.getExercises(searchNameInEnglish, muscle);

        if (englishResults == null || englishResults.isEmpty()) {
            return Collections.emptyList();
        }

        // 결과가 너무 많으면 성능 저하 방지를 위해 일부만 번역 (예: 최대 10개)
        List<ExerciseDTO> sublistToTranslate = englishResults.size() > 10 ? englishResults.subList(0, 10) : englishResults;

        List<TranslatedExerciseDTO> finalResults = new ArrayList<>();
        for (ExerciseDTO dto : sublistToTranslate) {
            String translatedName = translateWithPapago(dto.getName(), "en", "ko");
            finalResults.add(TranslatedExerciseDTO.builder()
                    .original(dto)
                    .translatedName(translatedName)
                    .build());
        }

        // 번역하지 않은 나머지 리스트가 있다면 추가 (이름만 영어로 표시)
        if (englishResults.size() > 10) {
            for (int i = 10; i < englishResults.size(); i++) {
                ExerciseDTO dto = englishResults.get(i);
                finalResults.add(TranslatedExerciseDTO.builder()
                        .original(dto)
                        .translatedName(dto.getName()) // 번역 없이 원본 이름 사용
                        .build());
            }
        }

        return finalResults;
    }

    /**
     * OpenAI -> Papago API를 사용하여 번역하도록 변경
     */
    @Override
    public TranslatedExerciseDTO getExerciseDetail(String name) throws Exception {
        log.info("운동 상세 정보 검색 시작: {}", name);
        List<ExerciseDTO> results = apiNinjasClient.getExercises(name, null);

        if (results == null || results.isEmpty()) {
            return null;
        }
        ExerciseDTO originalExercise = results.get(0);

        // Papago API를 호출하여 이름과 설명을 번역합니다.
        String translatedName = translateWithPapago(originalExercise.getName(), "en", "ko");
        String translatedInstructions = translateWithPapago(originalExercise.getInstructions(), "en", "ko");

        return TranslatedExerciseDTO.builder()
                .original(originalExercise)
                .translatedName(translatedName)
                .translatedInstructions(translatedInstructions)
                .build();
    }

    /**
     * Papago API를 호출하여 텍스트를 번역하는 private 메서드
     */
    private String translateWithPapago(String text, String sourceLang, String targetLang) {
        if (text == null || text.isBlank()) {
            return "";
        }
        try {
            String encodedText = URLEncoder.encode(text, StandardCharsets.UTF_8);
            String apiURL = "https://openapi.naver.com/v1/papago/n2mt";

            Map<String, String> requestHeaders = new HashMap<>();
            requestHeaders.put("X-Naver-Client-Id", papagoClientId);
            requestHeaders.put("X-Naver-Client-Secret", papagoClientSecret);
            // [수정] 아래 Content-Type 헤더를 반드시 추가해야 합니다!
            requestHeaders.put("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");

            String postParams = "source=" + sourceLang + "&target=" + targetLang + "&text=" + encodedText;

            String responseBody = NetworkUtil.post(apiURL, requestHeaders, postParams);

            // Papago API의 JSON 응답에서 번역된 텍스트만 추출
            JsonNode rootNode = objectMapper.readTree(responseBody);
            return rootNode.path("message").path("result").path("translatedText").asText();

        } catch (Exception e) {
            log.error("Papago 번역 중 오류 발생", e);
            return text; // 번역 실패 시 원본 텍스트 반환
        }
    }

    // 유튜브 검색 기능
    @Override
    public YoutubeDTO searchYoutube(String query) {
        log.info("YouTube 영상 검색 시작: {}", query);
        String searchQuery = query + " 운동 자세";
        return youtubeClient.searchVideos("snippet", searchQuery, "video", 4);
    }
}