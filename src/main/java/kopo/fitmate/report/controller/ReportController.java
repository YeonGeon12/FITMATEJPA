package kopo.fitmate.report.controller;

import kopo.fitmate.report.dto.ReportRequestDTO;
import kopo.fitmate.report.dto.ReportResponseDTO;
import kopo.fitmate.report.service.IReportService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
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
     * 사용자가 입력한 신체 정보를 받아 AI 분석을 요청하고 결과를 보여주는 메서드
     */
    @PostMapping("/getReport")
    public String getReport(ReportRequestDTO requestDTO, Model model) throws Exception {
        log.info("{}.getReport Start!", getClass().getName());

        // 1. Service를 호출하여 AI로부터 신체 분석 리포트 결과를 받음
        ReportResponseDTO responseDTO = reportService.getReport(requestDTO);

        // 2. 결과를 Model에 담아서 View(HTML)로 전달
        model.addAttribute("responseDTO", responseDTO);

        log.info("{}.getReport End!", getClass().getName());
        return "report/reportResult"; // templates/report/reportResult.html
    }
}