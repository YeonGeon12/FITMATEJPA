package kopo.fitmate.report.controller;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import kopo.fitmate.diet.dto.DietRequestDTO;
import kopo.fitmate.diet.dto.DietResponseDTO;
import kopo.fitmate.diet.dto.MealDTO;
import kopo.fitmate.report.dto.ReportRequestDTO;
import kopo.fitmate.report.dto.ReportResponseDTO;
import kopo.fitmate.report.service.IReportService;
import kopo.fitmate.user.dto.UserAuthDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
    // 1. getReport 메서드에 HttpSession 파라미터를 추가합니다.
    public String getReport(@Valid ReportRequestDTO requestDTO, BindingResult bindingResult,
                            HttpSession session, Model model) throws Exception {
        log.info("{}.getReport Start!", getClass().getName());

        if (bindingResult.hasErrors()) {
            log.info("Form validation errors found.");
            return "report/reportForm";
        }

        ReportResponseDTO responseDTO = reportService.getReport(requestDTO);

        // 2. AI로부터 받은 결과(responseDTO)와 사용자가 입력한 원본 값(requestDTO)을 세션에 저장합니다.
        //    이 데이터는 사용자가 '저장하기' 버튼을 누를 때까지 세션에 임시로 보관됩니다.
        session.setAttribute("latestReportRequest", requestDTO);
        session.setAttribute("latestReportResponse", responseDTO);

        model.addAttribute("responseDTO", responseDTO);

        log.info("{}.getReport End!", getClass().getName());
        return "report/reportResult";
    }


    /**
     * 세션에 저장된 AI 신체 분석 리포트를 DB에 저장하는 메서드
     */
    @PostMapping("/saveReport")
    public String saveReport(HttpSession session, @AuthenticationPrincipal UserAuthDTO user, Model model) {
        log.info("{}.saveReport Start!", getClass().getName());

        ReportRequestDTO requestDTO = (ReportRequestDTO) session.getAttribute("latestReportRequest");
        ReportResponseDTO responseDTO = (ReportResponseDTO) session.getAttribute("latestReportResponse");

        try {
            if (requestDTO != null && responseDTO != null) {
                reportService.saveReport(requestDTO, responseDTO, user);

                session.removeAttribute("latestReportRequest");
                session.removeAttribute("latestReportResponse");

                log.info("리포트 저장 성공!");
                model.addAttribute("successMsg", "리포트가 성공적으로 저장되었습니다!");

            } else {
                log.warn("세션에 저장된 리포트 정보가 없습니다.");
                model.addAttribute("errorMsg", "세션 정보가 만료되어 저장에 실패했습니다.");
            }
        } catch (Exception e) {
            log.error("리포트 저장 중 오류 발생", e);
            model.addAttribute("errorMsg", "저장 중 오류가 발생했습니다. 다시 시도해주세요.");
        }

        // ===== 수정된 부분: responseDTO를 Model에 다시 추가 =====
        // 저장 성공/실패와 관계없이 결과 페이지를 다시 보여줘야 하므로, 데이터를 Model에 담아줍니다.
        model.addAttribute("responseDTO", responseDTO);
        // ===== 수정 끝 =====

        log.info("{}.saveReport End!", getClass().getName());
        return "report/reportResult";
    }
}