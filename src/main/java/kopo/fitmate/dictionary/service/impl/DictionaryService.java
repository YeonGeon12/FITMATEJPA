package kopo.fitmate.dictionary.service.impl;

import kopo.fitmate.dictionary.client.ApiNinjasClient;
import kopo.fitmate.dictionary.client.YoutubeClient;
import kopo.fitmate.dictionary.dto.ExerciseDTO;
import kopo.fitmate.dictionary.dto.YoutubeDTO;
import kopo.fitmate.dictionary.dto.TranslatedExerciseDTO;
import kopo.fitmate.dictionary.service.IDictionaryService;
import kopo.fitmate.global.ai.OpenAiApiClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

/**
 * IDictionaryService의 구현체
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DictionaryService implements IDictionaryService {

    // Feign 클라이언트 의존성 주입
    private final ApiNinjasClient apiNinjasClient;
    private final YoutubeClient youtubeClient;
    private final OpenAiApiClient openAiApiClient;

    @Override
    public List<ExerciseDTO> searchExercises(String name, String muscle) {
        log.info("운동 정보 목록 검색 시작. 이름: {}, 부위: {}", name, muscle);

        String searchNameInEnglish = name; // 기본값은 원래 검색어

        // 이름으로 검색한 경우에만 번역 실행
        if (name != null && !name.isEmpty()) {
            try {
                // OpenAI API를 호출하여 한글 검색어를 영어로 번역
                searchNameInEnglish = openAiApiClient.chatText(
                        "You are a helpful translator.",
                        "Translate the following single fitness term to English. " +
                                "Provide only the translated English word and nothing else: " + name
                ).trim(); // trim()으로 불필요한 공백 제거
                log.info("한글 검색어 '{}' -> 영어 번역 '{}'", name, searchNameInEnglish);

            } catch (Exception e) {
                log.error("운동 이름 번역 중 오류 발생", e);
                // 번역 실패 시, 빈 리스트를 반환하여 결과 없음으로 처리
                return Collections.emptyList();
            }
        }

        // 번역된 영어 이름으로 API Ninjas 클라이언트를 호출
        return apiNinjasClient.getExercises(searchNameInEnglish, muscle);
    }

    @Override
    public TranslatedExerciseDTO getExerciseDetail(String name) throws Exception { // throws Exception 추가
        log.info("운동 상세 정보 검색 시작: {}", name);
        List<ExerciseDTO> results = apiNinjasClient.getExercises(name, null);

        if (results == null || results.isEmpty()) {
            return null; // 결과가 없으면 null 반환
        }

        ExerciseDTO originalExercise = results.get(0);

        // OpenAI API를 호출하여 이름과 설명을 번역합니다.
        String translatedName = openAiApiClient.chatText(
                "You are a helpful translator.",
                "Translate the following exercise name to Korean: " + originalExercise.getName()
        );
        String translatedInstructions = openAiApiClient.chatText(
                "You are a helpful translator.",
                "Translate the following exercise instructions to Korean: " + originalExercise.getInstructions()
        );

        // 원본 정보와 번역된 정보를 모두 담아 DTO를 생성하고 반환합니다.
        return TranslatedExerciseDTO.builder()
                .original(originalExercise)
                .translatedName(translatedName)
                .translatedInstructions(translatedInstructions)
                .build();
    }

    @Override
    public YoutubeDTO searchYoutube(String query) {
        log.info("YouTube 영상 검색 시작: {}", query);
        // 보다 정확한 검색을 위해 검색어에 "운동 자세" 추가
        String searchQuery = query + " 운동 자세";

        // YouTube 클라이언트를 호출하여 결과를 반환 (part='snippet', type='video', maxResults=6)
        return youtubeClient.searchVideos("snippet", searchQuery, "video", 6);
    }
}