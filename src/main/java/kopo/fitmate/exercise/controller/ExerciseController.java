package kopo.fitmate.exercise.controller;

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
     * 사용자가 입력한 폼 데이터를 받아 운동 추천을 처리하는 메서드
     * POST /exercise/getRecommendation
     */
    @PostMapping("/getRecommendation")
    public String getRecommendation(ExerciseRequestDTO requestDTO,
                                    @AuthenticationPrincipal UserAuthDTO user, Model model) throws Exception {

        log.info(this.getClass().getName() + ".getRecommendation Start!");

        // 1. Service를 호출하여 비즈니스 로직 수행
        ExerciseResponseDTO responseDTO = exerciseService.getExerciseRecommendation(requestDTO, user);

        // 2. Service로부터 받은 결과를 Model에 담아서 View로 전달
        model.addAttribute("responseDTO", responseDTO);

        log.info(this.getClass().getName() + ".getRecommendation End!");
        return "exercise/exerciseResult"; // templates/exercise/exerciseResult.html 파일을 보여줌
    }
}