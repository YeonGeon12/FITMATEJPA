package kopo.fitmate.exercise.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import kopo.fitmate.exercise.dto.ExerciseRequestDTO;
import kopo.fitmate.exercise.service.IExerciseAiService;
import kopo.fitmate.history.repository.entity.ExerciseInfoEntity;
import kopo.fitmate.history.service.IHistoryService;
import kopo.fitmate.user.dto.UserAuthDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/exercise")
public class ExerciseController {

    private final IExerciseAiService exerciseAiService;
    private final IHistoryService historyService; // 결과를 저장하기 위해 HistoryService 주입

    /**
     * 운동 추천 폼 페이지로 이동
     */
    @GetMapping("/exerciseForm")
    public String exerciseForm() {
        log.info(this.getClass().getName() + ".exerciseForm Start!");
        return "exercise/exerciseForm"; // templates/exercise/exerciseForm.html 반환
    }

    /**
     * AI 운동 추천 요청 처리 API
     * @return AI가 생성한 운동 루틴 JSON 문자열
     */
    @PostMapping("/api/recommend")
    @ResponseBody
    public ResponseEntity<String> getRecommendation(@RequestBody ExerciseRequestDTO pDTO) {
        log.info(this.getClass().getName() + ".getRecommendation Start!");
        try {
            String jsonResponse = exerciseAiService.getExerciseRecommendation(pDTO);
            return ResponseEntity.ok(jsonResponse);
        } catch (Exception e) {
            log.error("AI 운동 추천 중 오류 발생", e);
            // 클라이언트가 오류를 인지할 수 있도록 500 상태 코드와 오류 메시지를 JSON 형태로 반환
            return ResponseEntity.internalServerError().body("{\"error\": \"AI 추천 생성 중 오류가 발생했습니다.\"}");
        }
    }

    /**
     * 운동 추천 결과 페이지로 이동
     */
    @GetMapping("/exerciseResult ")
    public String exerciseResult () {
        log.info(this.getClass().getName() + ".exerciseResult  Start!");
        return "exercise/exerciseResult "; // templates/exercise/exerciseResult .html 반환
    }

    /**
     * 추천받은 운동 루틴 저장 API
     */
    @PostMapping("/api/save")
    @ResponseBody
    public ResponseEntity<String> saveRecommendation(@RequestBody Map<String, Object> payload,
                                                     @AuthenticationPrincipal UserAuthDTO user) {
        log.info(this.getClass().getName() + ".saveRecommendation Start!");
        try {
            ObjectMapper objectMapper = new ObjectMapper();

            // payload에서 requestParams와 routineDetails를 추출
            Map<String, Object> requestParams = objectMapper.convertValue(payload.get("requestParams"), new TypeReference<>() {});
            List<Map<String, Object>> routineDetails = objectMapper.convertValue(payload.get("routineDetails"), new TypeReference<>() {});
            String title = (String)requestParams.get("goal") + "을(를) 위한 맞춤 루틴";

            // ExerciseInfoEntity 빌드
            ExerciseInfoEntity pEntity = ExerciseInfoEntity.builder()
                    .userId(user.getUserNo())
                    .title(title)
                    .requestParams(requestParams)
                    .routineDetails(routineDetails)
                    .createdAt(new Date())
                    .build();

            // HistoryService를 통해 저장
            historyService.saveExerciseHistory(pEntity);

            return ResponseEntity.ok("{\"message\": \"루틴이 성공적으로 저장되었습니다.\"}");

        } catch (Exception e) {
            log.error("운동 루틴 저장 중 오류 발생", e);
            return ResponseEntity.internalServerError().body("{\"error\": \"저장 중 오류가 발생했습니다.\"}");
        }
    }
}