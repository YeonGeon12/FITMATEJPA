package kopo.fitmate.diet.controller;

import jakarta.servlet.http.HttpSession;
import kopo.fitmate.diet.dto.DietRequestDTO;
import kopo.fitmate.diet.dto.DietResponseDTO;
import kopo.fitmate.diet.dto.MealDTO;
import kopo.fitmate.user.dto.UserAuthDTO;
import kopo.fitmate.diet.service.IDietService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Controller
@RequestMapping("/diet") // '/diet'으로 시작하는 모든 요청은 이 컨트롤러가 처리
public class DietController {

    private final IDietService dietService;

    /**
     * 식단 추천 입력 폼 페이지로 이동하는 메서드
     * GET /diet/dietForm
     */
    @GetMapping("/dietForm")
    public String dietForm(Model model) {
        log.info(this.getClass().getName() + ".dietForm Start!");

        // HTML 폼(Thymeleaf)에서 데이터를 바인딩하기 위해 비어있는 DTO 객체를 모델에 담아서 전달
        model.addAttribute("requestDTO", new DietRequestDTO());

        log.info(this.getClass().getName() + ".dietForm End!");
        return "diet/dietForm"; // templates/diet/dietForm.html 뷰를 반환
    }

    /**
     * AI에게 추천을 요청하고, 결과를 세션에 저장한 뒤 결과 페이지를 보여주는 메서드
     */
    @PostMapping("/getRecommendation")
    public String getRecommendation(DietRequestDTO requestDTO, HttpSession session, Model model) throws Exception {
        log.info(this.getClass().getName() + ".getRecommendation Start!");

        DietResponseDTO responseDTO = dietService.getDietRecommendation(requestDTO);

        // 1. 추천 요청 및 결과 데이터를 세션에 임시 저장
        session.setAttribute("latestDietRequest", requestDTO);
        session.setAttribute("latestDietResponse", responseDTO);

        // 2. 표(table)를 그리기 위해 데이터를 '요일'별로 그룹화
        Map<String, List<MealDTO>> dietMap = responseDTO.getWeeklyDiet().stream()
                .collect(Collectors.groupingBy(MealDTO::getDay, LinkedHashMap::new, Collectors.toList()));

        model.addAttribute("dietMap", dietMap);

        log.info(this.getClass().getName() + ".getRecommendation End!");
        return "diet/dietResult";
    }

    /**
     * 세션에 저장된 식단 추천 결과를 DB에 저장하는 메서드
     */
    @PostMapping("/saveRecommendation")
    public String saveRecommendation(HttpSession session, @AuthenticationPrincipal UserAuthDTO user, Model model) {
        log.info(this.getClass().getName() + ".saveRecommendation Start!");

        // 1. 세션에서 데이터 가져오기
        DietRequestDTO requestDTO = (DietRequestDTO) session.getAttribute("latestDietRequest");
        DietResponseDTO responseDTO = (DietResponseDTO) session.getAttribute("latestDietResponse");

        try {
            if (requestDTO != null && responseDTO != null) {
                // 2. Service를 호출하여 DB에 저장
                dietService.saveDietRecommendation(requestDTO, responseDTO, user);

                // 3. 사용 후 세션 데이터 삭제 (중복 저장 방지)
                session.removeAttribute("latestDietRequest");
                session.removeAttribute("latestDietResponse");

                log.info("식단 저장 성공!");
                // 성공 메시지를 Model에 추가
                model.addAttribute("successMsg", "식단이 성공적으로 저장되었습니다!");

            } else {
                log.warn("세션에 저장된 추천 정보가 없습니다.");
                // 실패 메시지를 Model에 추가
                model.addAttribute("errorMsg", "세션 정보가 만료되어 저장에 실패했습니다.");
            }
        } catch (Exception e) {
            log.error("식단 저장 중 오류 발생", e);
            // 에러 메시지를 Model에 추가
            model.addAttribute("errorMsg", "저장 중 오류가 발생했습니다. 다시 시도해주세요.");
        }

        // 4. 모달창을 띄운 후에도 기존 결과 테이블을 보여주기 위해 Map 데이터 다시 추가
        if (responseDTO != null) {
            Map<String, List<MealDTO>> dietMap = responseDTO.getWeeklyDiet().stream()
                    .collect(Collectors.groupingBy(MealDTO::getDay, LinkedHashMap::new, Collectors.toList()));
            model.addAttribute("dietMap", dietMap);
        }

        log.info(this.getClass().getName() + ".saveRecommendation End!");

        // 5. 항상 dietResult 페이지로 이동하여 모달창을 띄움
        return "diet/dietResult";
    }
}