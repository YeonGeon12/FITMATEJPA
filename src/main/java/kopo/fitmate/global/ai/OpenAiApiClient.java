package kopo.fitmate.global.ai;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import kopo.fitmate.exercise.dto.ExerciseRequestDTO;
import kopo.fitmate.exercise.dto.ExerciseResponseDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class OpenAiApiClient {

    private final ObjectMapper objectMapper;

    // application.yaml 에 정의된 OpenAI URL 사용 (기본: v1/chat/completions)
    @Value("${openai.api.url:https://api.openai.com/v1/chat/completions}")
    private String apiUrl;

    // 환경변수 또는 YAML placeholder로 주입
    @Value("${openai.api.key:${OPENAI_API_KEY:}}")
    private String apiKey;

    /**
     * Service에서 호출하는 기존 시그니처 유지
     */
    public ExerciseResponseDTO getRecommendation(ExerciseRequestDTO requestDTO) throws Exception {
        log.info("OpenAI -> getRecommendation Start");

        if (apiKey == null || apiKey.isBlank()) {
            log.error("OPENAI API Key가 설정되어 있지 않습니다. 환경변수 OPENAI_API_KEY를 확인하세요.");
            throw new IllegalStateException("OPENAI API Key is not set");
        }

        String prompt = createPrompt(requestDTO);

        // Chat Completions 요청 페이로드
        Map<String, Object> payload = Map.of(
                "model", "gpt-4o", // 필요 시 "gpt-5" 등으로 교체하여 테스트
                "messages", new Object[] {
                        Map.of("role", "system", "content", "You are a professional fitness trainer."),
                        Map.of("role", "user", "content", prompt)
                },
                "max_tokens", 800,
                "temperature", 0.2
        );

        String requestBody = objectMapper.writeValueAsString(payload);

        HttpClient client = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();

        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(URI.create(apiUrl))
                .timeout(Duration.ofSeconds(30))
                .header("Authorization", "Bearer " + apiKey)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();

        HttpResponse<String> response = client.send(httpRequest, HttpResponse.BodyHandlers.ofString());

        int status = response.statusCode();
        String responseBody = response.body();
        log.debug("OpenAI response status: {}, body: {}", status, responseBody);

        if (status == 401) {
            throw new RuntimeException("Unauthorized - OpenAI API key invalid or not permitted.");
        }
        if (status >= 400) {
            throw new RuntimeException("OpenAI API error: HTTP " + status + " - " + responseBody);
        }

        // choices[0].message.content 추출
        JsonNode root = objectMapper.readTree(responseBody);
        JsonNode choices = root.path("choices");
        if (!choices.isArray() || choices.size() == 0) {
            log.error("OpenAI로부터 유효한 choices를 받지 못했습니다. 응답: {}", responseBody);
            throw new RuntimeException("Invalid response from OpenAI");
        }
        String assistantContent = null;
        JsonNode firstChoice = choices.get(0);
        if (firstChoice.has("message")) {
            assistantContent = firstChoice.path("message").path("content").asText(null);
        } else if (firstChoice.has("text")) { // 호환용
            assistantContent = firstChoice.path("text").asText(null);
        }
        if (assistantContent == null || assistantContent.isBlank()) {
            log.error("OpenAI 응답에서 assistant content를 추출하지 못했습니다. body={}", responseBody);
            throw new RuntimeException("No content from OpenAI response");
        }

        // 코드펜스 제거 후 JSON만 추출
        String cleanedJson = assistantContent
                .replaceAll("(?s)^```(?:json)?\\s*", "")
                .replaceAll("\\s*```\\s*$", "")
                .trim();

        try {
            ExerciseResponseDTO dto = objectMapper.readValue(cleanedJson, ExerciseResponseDTO.class);
            log.info("OpenAI -> getRecommendation End (success)");
            return dto;
        } catch (Exception ex) {
            log.error("ExerciseResponseDTO 역직렬화 실패. cleanedJson: {}", cleanedJson, ex);
            throw new RuntimeException("Failed to parse exercise JSON from OpenAI response. raw: " + assistantContent, ex);
        }
    }

    /**
     * 기존 프롬프트 생성 로직(필요 시 수정)
     */
    private String createPrompt(ExerciseRequestDTO dto) {
        return String.format(
                "너는 사용자의 건강 데이터를 기반으로 맞춤형 운동 루틴을 추천하는 전문 헬스 트레이너야. " +
                        "아래 사용자 정보를 바탕으로, 반드시 'JSON 형식'으로만 주간 운동 루틴을 생성해줘.\n\n" +
                        "## 사용자 정보:\n" +
                        "- 키: %dcm\n" +
                        "- 체중: %dkg\n" +
                        "- 성별: %s\n" +
                        "- 나이: %d세\n" +
                        "- 운동 수준: %s\n" +
                        "- 운동 목표: %s\n" +
                        "- 운동 가능 장소: %s\n" +
                        "- 집중 희망 부위: %s\n\n" +
                        "## 출력 JSON 형식(중요): weeklyRoutine 배열, 각 요소는 " +
                        "'day','bodyPart','exerciseName','sets','reps','restTime' 6개 키를 모두 포함. " +
                        "쉬는 날은 bodyPart='휴식', 나머지는 '-'로 채워줘.\n\n" +
                        "예시:\n" +
                        "```json\n" +
                        "{ \"weeklyRoutine\": [ { \"day\":\"월\", \"bodyPart\":\"가슴\", \"exerciseName\":\"벤치프레스\", \"sets\":\"4\", \"reps\":\"10-12\", \"restTime\":\"60초\" } ] }\n" +
                        "```\n" +
                        "위 조건을 철저히 지켜서 **JSON만** 출력해줘.",
                dto.getHeight(), dto.getWeight(), dto.getGender(), dto.getAge(),
                dto.getExerciseLevel(), dto.getExerciseGoal(), dto.getWorkoutLocation(),
                String.join(", ", dto.getBodyParts())
        );
    }
}
