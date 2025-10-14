package kopo.fitmate.report.controller;

import jakarta.validation.Valid;
import kopo.fitmate.report.dto.ReportRequestDTO;
import kopo.fitmate.report.dto.ReportResponseDTO;
import kopo.fitmate.report.service.IReportService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Slf4j
@Controller
@RequestMapping("/report")
@RequiredArgsConstructor
public class ReportController {

    private final IReportService reportService;

    /**
     * AI 신체 분석 정보 입력 폼 페이지로 이동하는 메서드
     */
    @GetMapping("/reportForm")
    public String reportForm(Model model) {
        log.info("{}.reportForm Start!", getClass().getName());

        // 폼 바인딩을 위해 비어있는 DTO를 모델에 추가
        model.addAttribute("requestDTO", new ReportRequestDTO());

        log.info("{}.reportForm End!", getClass().getName());
        return "report/reportForm"; // templates/report/reportForm.html
    }

    /**
     * 사용자가 입력한 신체 정보를 '유효성 검사'한 후, AI 분석을 요청하고 결과를 보여주는 메서드
     */
    @PostMapping("/getReport")
    public String getReport(@Valid ReportRequestDTO requestDTO, BindingResult bindingResult, Model model) throws Exception {
        log.info("{}.getReport Start!", getClass().getName());

        // 1. 유효성 검사 결과, 오류가 있다면 폼 페이지로 다시 돌아감
        if (bindingResult.hasErrors()) {
            log.info("Form validation errors found.");
            return "report/reportForm"; // 오류 메시지와 함께 reportForm.html을 다시 보여줌
        }

        // 2. 오류가 없다면, 기존 로직 수행
        ReportResponseDTO responseDTO = reportService.getReport(requestDTO);
        model.addAttribute("responseDTO", responseDTO);

        log.info("{}.getReport End!", getClass().getName());
        return "report/reportResult";
    }
}