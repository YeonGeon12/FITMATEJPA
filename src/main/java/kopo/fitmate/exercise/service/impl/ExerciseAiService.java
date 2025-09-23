// src/main/java/kopo/fitmate/exercise/service/impl/ExerciseAiService.java

package kopo.fitmate.exercise.service.impl;

// [최종 수정] 필요한 클래스만 정확하게 import 합니다.
import com.google.common.util.concurrent.ListenableFuture;
import com.google.genai.GenerativeModel;
import com.google.genai.GenerationConfig;
import com.google.genai.HarmCategory;
import com.google.genai.SafetySetting;
import com.google.genai.responses.GenerateContentResponse;
import com.google.genai.responses.ResponseHandler;

import kopo.fitmate.exercise.service.IExerciseAiService;
import kopo.fitmate.exercise.dto.ExerciseRequestDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

@Slf4j
@Service
public class ExerciseAiService implements IExerciseAiService {

    @Value("${gemini.api.key}")
    private String geminiApiKey;

    @Override
    public String getExerciseRecommendation(ExerciseRequestDTO pDTO) throws Exception {

        log.info(this.getClass().getName() + ".getExerciseRecommendation Start!");

        String result;

        try {
            // 1. AI 모델 설정
            GenerativeModel model = createGenerativeModel();

            // 2. AI에게 전달할 프롬프트 생성
            String prompt = generatePrompt(pDTO);
            log.info("Generated Prompt: \n" + prompt);

            // 3. AI 모델에 프롬프트를 보내고 응답을 비동기적으로 받음
            Executor executor = Executors.newSingleThreadExecutor();
            ListenableFuture<GenerateContentResponse> responseFuture = model.generateContent(prompt, executor);

            // 4. 비동기 응답을 동기적으로 기다린 후, 텍스트만 추출
            // [최종 수정] ResponseHandler.getText()를 사용하여 매우 간단하게 텍스트를 가져옵니다.
            String responseText = ResponseHandler.getText(responseFuture.get());
            log.info("Gemini API Response: \n" + responseText);

            // 5. 응답 결과에서 순수한 JSON 부분만 깔끔하게 추출
            result = extractJson(responseText);


        } catch (Exception e) {
            log.error("Gemini API 호출 중 에러 발생: " + e.getMessage(), e);
            throw new RuntimeException("AI 모델을 호출하는 중에 문제가 발생했습니다.", e);
        }

        log.info(this.getClass().getName() + ".getExerciseRecommendation End!");

        return result;
    }

    /**
     * Gemini API 요청을 위한 GenerativeModel 객체를 생성하고 설정합니다.
     */
    private GenerativeModel createGenerativeModel() {
        return new GenerativeModel(
                "gemini-pro",
                geminiApiKey,
                GenerationConfig.newBuilder()
                        .setTemperature(0.7f)
                        .setTopP(1.0f)
                        .setTopK(1)
                        .setMaxOutputTokens(2048)
                        .build(),
                Collections.singletonList(
                        SafetySetting.newBuilder()
                                .setCategory(HarmCategory.HARASSMENT)
                                .setThreshold(SafetySetting.HarmBlockThreshold.BLOCK_NONE)
                                .build()
                )
        );
    }

    /**
     * DTO를 기반으로 Gemini API에 전달할 프롬프트를 생성합니다.
     */
    private String generatePrompt(ExerciseRequestDTO pDTO) {
        // 프롬프트 내용은 이전과 동일하게 유지합니다.
        return "당신은 20년 경력의 베테랑 헬스 트레이너입니다. "
                + "다음 사용자의 정보에 맞춰 7일간의 운동 루틴을 JSON 형식으로만 추천해주세요. "
                + "각 운동은 'exerciseName'(운동이름), 'sets'(세트), 'reps'(횟수), 'restTimeInSeconds'(세트간 휴식시간) 키를 반드시 포함해야 합니다. "
                + "최상위 키는 'weekRoutine' 이어야 하고, 그 값은 'day1' 부터 'day7' 까지의 키를 가진 JSON 객체여야 합니다. "
                + "각 날짜의 값은 운동 객체들의 배열(리스트)이어야 합니다. "
                + "운동 이름은 한국어로, 다른 설명 없이 오직 JSON 데이터만 응답해주세요.\n"
                + "--- 사용자 정보 ---\n"
                + "- 운동 목표: " + pDTO.getGoal() + "\n"
                + "- 운동 레벨: " + pDTO.getLevel() + "\n"
                + "- 운동 가능 장소: " + pDTO.getLocation() + "\n"
                + "- 집중 희망 부위: " + pDTO.getTargetArea() + "\n"
                + "--------------------";
    }

    /**
     * AI 응답 텍스트에서 JSON 부분만 정확히 추출합니다.
     */
    private String extractJson(String response) {
        int startIndex = response.indexOf("{");
        int endIndex = response.lastIndexOf("}");
        if (startIndex != -1 && endIndex != -1 && startIndex < endIndex) {
            return response.substring(startIndex, endIndex + 1);
        }
        log.warn("응답에서 유효한 JSON 객체를 찾을 수 없습니다. Response: " + response);
        return "{}";
    }
}