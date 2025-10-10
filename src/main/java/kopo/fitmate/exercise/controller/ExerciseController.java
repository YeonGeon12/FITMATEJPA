package kopo.fitmate.exercise.controller;

import jakarta.servlet.http.HttpSession; // HttpSession import 추가
import kopo.fitmate.exercise.dto.ExerciseRequestDTO;
import kopo.fitmate.exercise.dto.ExerciseResponseDTO;
import kopo.fitmate.exercise.service.IExerciseService;
import kopo.fitmate.user.dto.UserAuthDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Slf4j
@RequiredArgsConstructor
@Controller
@RequestMapping("/exercise")
public class ExerciseController {

    private final IExerciseService exerciseService;

    /**
     * 운동 추천 입력 폼 페이지로 이동하는 메서드
     * GET /exercise/exerciseForm
     */
    @GetMapping("/exerciseForm")
    public String exerciseForm(Model model) {
        log.info(this.getClass().getName() + ".exerciseForm Start!");

        // HTML 폼에서 th:object와 데이터를 연결하기 위해 비어있는 DTO 객체를 모델에 담아서 전달
        model.addAttribute("requestDTO", new ExerciseRequestDTO());

        log.info(this.getClass().getName() + ".exerciseForm End!");
        return "exercise/exerciseForm"; // templates/exercise/exerciseForm.html 파일을 보여줌
    }

    /**
     * 사용자가 입력한 폼 데이터를 받아 운동 추천을 처리하고, 결과를 세션에 임시 저장하는 메서드
     * POST /exercise/getRecommendation
     */
    @PostMapping("/getRecommendation")
    public String getRecommendation(ExerciseRequestDTO requestDTO, HttpSession session, // HttpSession 파라미터 추가
                                    @AuthenticationPrincipal UserAuthDTO user, Model model) throws Exception {

        log.info(this.getClass().getName() + ".getRecommendation Start!");

        // 1. Service를 호출하여 AI 추천 결과 받기
        ExerciseResponseDTO responseDTO = exerciseService.getExerciseRecommendation(requestDTO, user);

        // 2. 추천에 사용된 요청 데이터(requestDTO)와 결과 데이터(responseDTO)를 세션에 저장
        //    (Redis에 직렬화되어 저장됩니다)
        session.setAttribute("latestRequest", requestDTO);
        session.setAttribute("latestResponse", responseDTO);

        // 3. Service로부터 받은 결과를 Model에 담아서 View로 전달
        model.addAttribute("responseDTO", responseDTO);

        log.info(this.getClass().getName() + ".getRecommendation End!");
        return "exercise/exerciseResult"; // templates/exercise/exerciseResult.html 파일을 보여줌
    }


    /**
     * 세션의 운동 루틴을 DB에 저장하고, 결과를 exerciseResult 뷰에 전달하는 메서드
     */
    @PostMapping("/saveRecommendation")
    public String saveRecommendation(HttpSession session, @AuthenticationPrincipal UserAuthDTO user, Model model) {
        log.info(this.getClass().getName() + ".saveRecommendation Start!");

        // 1. 세션에서 데이터 가져오기
        ExerciseRequestDTO requestDTO = (ExerciseRequestDTO) session.getAttribute("latestRequest");
        ExerciseResponseDTO responseDTO = (ExerciseResponseDTO) session.getAttribute("latestResponse");

        try {
            if (requestDTO != null && responseDTO != null) {
                // 2. Service를 호출하여 DB에 저장
                exerciseService.saveExerciseRecommendation(requestDTO, responseDTO, user);

                // 3. 사용 후 세션 데이터 삭제
                session.removeAttribute("latestRequest");
                session.removeAttribute("latestResponse");

                log.info("루틴 저장 성공!");
                // 성공 메시지를 Model에 추가
                model.addAttribute("successMsg", "운동 루틴이 성공적으로 저장되었습니다!");

            } else {
                log.warn("세션에 저장된 추천 정보가 없습니다.");
                // 실패 메시지를 Model에 추가
                model.addAttribute("errorMsg", "세션 정보가 만료되어 저장에 실패했습니다.");
            }

        } catch (Exception e) {
            log.error("루틴 저장 중 오류 발생", e);
            // 에러 메시지를 Model에 추가
            model.addAttribute("errorMsg", "저장 중 오류가 발생했습니다. 다시 시도해주세요.");
        }

        // 4. 결과를 다시 보여주기 위해 responseDTO를 Model에 추가
        //    (성공/실패 모든 경우에 뷰를 다시 렌더링해야 하므로)
        model.addAttribute("responseDTO", responseDTO);

        log.info(this.getClass().getName() + ".saveRecommendation End!");

        // 5. 항상 exerciseResult 페이지로 이동
        return "exercise/exerciseResult";
    }
}