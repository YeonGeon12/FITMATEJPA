package kopo.fitmate.global.ai;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.List;
import java.util.Map;

/** 모든 도메인(운동/식단/신체분석)에서 공용으로 쓰는 OpenAI 클라이언트 */
@Slf4j
@Component
@RequiredArgsConstructor
public class OpenAiApiClient {

    private final ObjectMapper objectMapper;

    @Value("${openai.api.url:https://api.openai.com/v1/chat/completions}")
    private String apiUrl;

    @Value("${openai.api.key:${OPENAI_API_KEY:}}")
    private String apiKey;

    @Value("${openai.api.model:gpt-4o}")
    private String defaultModel;

    @Value("${openai.api.max-tokens:1200}")
    private Integer defaultMaxTokens;

    @Value("${openai.api.temperature:0.3}")
    private Double defaultTemperature;

    /** JSON만 강제해서 받고 지정 DTO 타입으로 역직렬화 */
    public <T> T chatJson(String system, String user, Class<T> responseType) throws Exception {
        return chatJson(system, user, responseType, defaultModel, defaultMaxTokens, defaultTemperature, true);
    }

    public <T> T chatJson(String system,
                          String user,
                          Class<T> responseType,
                          String model,
                          Integer maxTokens,
                          Double temperature,
                          boolean forceJsonObject) throws Exception {

        ensureApiKey();

        Map<String, Object> payload = Map.of(
                "model", model != null ? model : defaultModel,
                "messages", new Object[]{
                        Map.of("role", "system", "content", system != null ? system : ""),
                        Map.of("role", "user", "content", user != null ? user : "")
                },
                "max_tokens", maxTokens != null ? maxTokens : defaultMaxTokens,
                "temperature", temperature != null ? temperature : defaultTemperature
        );

        String requestBody = forceJsonObject
                ? objectMapper.writeValueAsString(new java.util.LinkedHashMap<>() {{
            putAll(payload);
            put("response_format", Map.of("type", "json_object"));
        }})
                : objectMapper.writeValueAsString(payload);

        String raw = callOpenAi(requestBody);
        String cleaned = extractJson(raw);
        return objectMapper.readValue(cleaned, responseType);
    }

    /** 자유 텍스트(마크다운 등)로 받고 싶을 때 */
    public String chatText(String system, String user) throws Exception {
        ensureApiKey();

        Map<String, Object> payload = Map.of(
                "model", defaultModel,
                "messages", List.of(
                        Map.of("role", "system", "content", system != null ? system : ""),
                        Map.of("role", "user", "content", user != null ? user : "")
                ),
                "max_tokens", defaultMaxTokens,
                "temperature", defaultTemperature
        );

        String raw = callOpenAi(objectMapper.writeValueAsString(payload));
        return extractAssistantText(raw);
    }

    // -------------------- 내부 공통 --------------------

    private String callOpenAi(String requestBody) throws Exception {
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
        String body = response.body();
        log.debug("[OpenAI] status={}, body={}", status, body);

        if (status == 401) throw new RuntimeException("Unauthorized - OPENAI_API_KEY 확인 필요");
        if (status >= 400) throw new RuntimeException("OpenAI API error: HTTP " + status + " - " + body);

        JsonNode root = objectMapper.readTree(body);
        JsonNode choices = root.path("choices");
        if (!choices.isArray() || choices.size() == 0) {
            throw new RuntimeException("No choices in OpenAI response");
        }
        JsonNode first = choices.get(0);
        if (first.has("message")) {
            return first.path("message").path("content").asText("");
        } else if (first.has("text")) {
            return first.path("text").asText("");
        }
        return "";
    }

    private void ensureApiKey() {
        if (apiKey == null || apiKey.isBlank()) {
            throw new IllegalStateException("OpenAI API key is missing. Set OPENAI_API_KEY.");
        }
    }

    /** 코드펜스 제거(```json ... ```) */
    private String extractJson(String raw) {
        return raw.replaceAll("(?s)^```(?:json)?\\s*", "")
                .replaceAll("\\s*```\\s*$", "")
                .trim();
    }

    /** 자유 텍스트용 코드펜스 제거 */
    private String extractAssistantText(String raw) {
        return raw.replaceAll("(?s)^```(?:\\w+)?\\s*", "")
                .replaceAll("\\s*```\\s*$", "")
                .trim();
    }
}
