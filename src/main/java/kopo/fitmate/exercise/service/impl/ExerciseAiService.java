package kopo.fitmate.exercise.service.impl;

import com.google.genai.Client;
import com.google.genai.types.GenerateContentResponse;
import kopo.fitmate.exercise.dto.ExerciseRequestDTO;
import kopo.fitmate.exercise.service.IExerciseAiService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class ExerciseAiService implements IExerciseAiService {

    @Value("${gemini.api.key}")
    private String geminiApiKey;

    @Override
    public String getExerciseRecommendation(ExerciseRequestDTO pDTO) throws Exception {
        log.info("ExerciseAiService.getExerciseRecommendation Start");

        try {
            String prompt = generatePrompt(pDTO);
            log.info("Generated Prompt:\n{}", prompt);

            Client client = new Client.Builder()
                    .apiKey(geminiApiKey)
                    .build();

            GenerateContentResponse response =
                    client.models.generateContent("gemini-2.5-flash", prompt, null);

            String responseText = response.text() != null ? response.text() : "";
            return extractJson(responseText);

        } catch (Exception e) {
            log.error("Gemini API 호출 중 에러 발생: {}", e.getMessage(), e);
            throw new RuntimeException("AI 모델을 호출하는 중에 문제가 발생했습니다.", e);
        } finally {
            log.info("ExerciseAiService.getExerciseRecommendation End");
        }
    }

    private String generatePrompt(ExerciseRequestDTO pDTO) {
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

    private String extractJson(String response) {
        String clean = response.replace("```json", "").replace("```", "").trim();
        int s = clean.indexOf("{");
        int e = clean.lastIndexOf("}");
        if (s != -1 && e != -1 && s < e) return clean.substring(s, e + 1);
        log.warn("응답에서 유효한 JSON 객체를 찾을 수 없습니다. Response: {}", clean);
        return "{}";
    }
}
