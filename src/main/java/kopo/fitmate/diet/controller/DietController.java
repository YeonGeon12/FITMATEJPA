package kopo.fitmate.diet.controller;

import kopo.fitmate.diet.dto.DietRequestDTO;
import kopo.fitmate.diet.dto.DietResponseDTO;
import kopo.fitmate.diet.dto.MealDTO;
import kopo.fitmate.diet.service.IDietService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
     * 사용자가 선택한 식단 유형을 받아 AI에게 추천을 요청하고 결과를 보여주는 메서드
     * POST /diet/getRecommendation
     */
    @PostMapping("/getRecommendation")
    public String getRecommendation(DietRequestDTO requestDTO, Model model) throws Exception {
        log.info(this.getClass().getName() + ".getRecommendation Start!");

        // 1. Service를 호출하여 AI로부터 식단 추천 결과를 받음
        DietResponseDTO responseDTO = dietService.getDietRecommendation(requestDTO);

        // ===== 수정된 부분: 데이터를 '요일'별로 그룹화 =====
        // LinkedHashMap: 순서가 보장되는 Map (월, 화, 수... 순서 유지)
        Map<String, List<MealDTO>> dietMap = responseDTO.getWeeklyDiet().stream()
                .collect(Collectors.groupingBy(MealDTO::getDay, LinkedHashMap::new, Collectors.toList()));

        // 가공된 Map 데이터를 Model에 추가
        model.addAttribute("dietMap", dietMap);
        // ===== 수정 끝 =====

        log.info(this.getClass().getName() + ".getRecommendation End!");
        return "diet/dietResult"; // templates/diet/dietResult.html 뷰를 반환
    }
}