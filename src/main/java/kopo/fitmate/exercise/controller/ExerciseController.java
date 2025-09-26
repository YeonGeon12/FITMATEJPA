package kopo.fitmate.exercise.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import kopo.fitmate.exercise.dto.ExerciseRequestDTO;
import kopo.fitmate.exercise.service.IExerciseAiService;
import kopo.fitmate.history.repository.entity.ExerciseInfoEntity;
import kopo.fitmate.history.service.IHistoryService;
import kopo.fitmate.user.dto.UserAuthDTO;
import kopo.fitmate.user.dto.UserProfileDTO;
import kopo.fitmate.user.service.IUserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/exercise")
public class ExerciseController {

    private final IExerciseAiService exerciseAiService;
    private final IHistoryService historyService;
    private final IUserService userService; // 사용자 프로필 조회를 위해 IUserService 주입

    /**
     * 운동 추천 폼 페이지로 이동
     * [수정] 사용자 프로필 정보를 조회하여 폼에 미리 채워주는 로직 추가
     */
    @GetMapping("/exerciseForm")
    public String exerciseForm(@AuthenticationPrincipal UserAuthDTO user, Model model, RedirectAttributes rttr) {
        log.info(this.getClass().getName() + ".exerciseForm Start!");

        // 1. 현재 로그인한 사용자의 프로필 정보를 조회합니다.
        UserProfileDTO pDTO = userService.getUserProfile(user.getUserNo());

        // 2. 프로필 정보(특히 키)가 없으면, 프로필 입력 페이지로 리다이렉트 시킵니다.
        if (pDTO.getHeight() == null) {
            log.info("프로필 정보가 없어 프로필 입력 페이지로 이동합니다.");
            // 사용자에게 안내 메시지를 전달
            rttr.addFlashAttribute("msg", "AI 추천을 받기 위해 먼저 신체 정보를 입력해주세요.");
            return "redirect:/user/profileForm"; // 프로필 입력 폼으로 강제 이동
        }

        // 3. 프로필 정보가 있으면, Model에 담아 View(HTML)로 전달합니다.
        log.info("프로필 정보 로드 완료. 폼 페이지로 전달합니다.");
        model.addAttribute("userProfile", pDTO);

        return "exercise/exerciseForm";
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
            return ResponseEntity.internalServerError().body("{\"error\": \"AI 추천 생성 중 오류가 발생했습니다.\"}");
        }
    }

    /**
     * 운동 추천 결과 페이지로 이동
     */
    @GetMapping("/exerciseResult")
    public String exerciseResult() {
        log.info(this.getClass().getName() + ".exerciseResult Start!");
        return "exercise/exerciseResult";
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

            Map<String, Object> requestParams = objectMapper.convertValue(payload.get("requestParams"), new TypeReference<>() {});
            List<Map<String, Object>> routineDetails = objectMapper.convertValue(payload.get("routineDetails"), new TypeReference<>() {});
            String title = (String)requestParams.get("goal") + "을(를) 위한 맞춤 루틴";

            ExerciseInfoEntity pEntity = ExerciseInfoEntity.builder()
                    .userId(user.getUserNo())
                    .title(title)
                    .requestParams(requestParams)
                    .routineDetails(routineDetails)
                    .createdAt(new Date())
                    .build();

            historyService.saveExerciseHistory(pEntity);

            return ResponseEntity.ok("{\"message\": \"루틴이 성공적으로 저장되었습니다.\"}");

        } catch (Exception e) {
            log.error("운동 루틴 저장 중 오류 발생", e);
            return ResponseEntity.internalServerError().body("{\"error\": \"저장 중 오류가 발생했습니다.\"}");
        }
    }
}