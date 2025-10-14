package kopo.fitmate.history.controller;

import kopo.fitmate.diet.repository.entity.MealEmbed;
import kopo.fitmate.history.dto.DietDetailDTO;
import kopo.fitmate.history.dto.ExerciseDetailDTO;
import kopo.fitmate.history.dto.HistoryViewDTO;
import kopo.fitmate.history.service.IHistoryService;
import kopo.fitmate.report.repository.entity.ReportInfoEntity;
import kopo.fitmate.user.dto.UserAuthDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * '내 기록 보기' 기능과 관련된 웹 요청을 처리하는 컨트롤러입니다.
 */
@Slf4j
@Controller
@RequestMapping("/history")
@RequiredArgsConstructor
public class HistoryController {

    private final IHistoryService historyService;

    /**
     * '내 기록 보기' 페이지로 이동
     */
    @GetMapping("/view")
    public String historyView(@AuthenticationPrincipal UserAuthDTO user, Model model) {
        log.info("{}.historyView Start!", getClass().getName());
        HistoryViewDTO historyData = historyService.getHistoryList(user);
        model.addAttribute("historyData", historyData);
        log.info("{}.historyView End!", getClass().getName());
        return "history/historyView";
    }

    /**
     * 운동 루틴 상세 보기 페이지로 이동
     */
    @GetMapping("/exercise/{id}")
    public String exerciseDetail(@PathVariable String id, @AuthenticationPrincipal UserAuthDTO user, Model model) {
        log.info("{}.exerciseDetail Start!", getClass().getName());
        ExerciseDetailDTO detailData = historyService.getExerciseDetail(id, user);
        model.addAttribute("detailData", detailData);
        log.info("{}.exerciseDetail End!", getClass().getName());
        return "history/exerciseDetail";
    }

    /**
     * 식단 상세 보기 페이지로 이동
     */
    @GetMapping("/diet/{id}")
    public String dietDetail(@PathVariable String id, @AuthenticationPrincipal UserAuthDTO user, Model model) {
        log.info("{}.dietDetail Start!", getClass().getName());
        DietDetailDTO detailData = historyService.getDietDetail(id, user);
        if (detailData != null) {
            Map<String, List<MealEmbed>> dietMap = detailData.getWeeklyDiet().stream()
                    .collect(Collectors.groupingBy(MealEmbed::getDay, LinkedHashMap::new, Collectors.toList()));
            model.addAttribute("dietMap", dietMap);
        }
        model.addAttribute("detailData", detailData);
        log.info("{}.dietDetail End!", getClass().getName());
        return "history/dietDetail";
    }

    /**
     *  AI 리포트 상세 보기 페이지로 이동
     */
    @GetMapping("/report/{id}")
    public String reportDetail(@PathVariable String id, @AuthenticationPrincipal UserAuthDTO user, Model model) {
        log.info("{}.reportDetail Start!", getClass().getName());

        // 서비스의 getReportDetail 메서드를 호출하여 상세 정보를 가져옴
        ReportInfoEntity detailData = historyService.getReportDetail(id, user);

        // 가져온 데이터를 'detailData'라는 이름으로 Model에 담아 View로 전달
        model.addAttribute("detailData", detailData);

        log.info("{}.reportDetail End!", getClass().getName());
        return "history/reportDetail"; // templates/history/reportDetail.html 파일을 보여줌
    }

    /**
     * 운동 루틴 기록 삭제 처리
     */
    @PostMapping("/exercise/delete/{id}")
    public String deleteExerciseHistory(@PathVariable String id, @AuthenticationPrincipal UserAuthDTO user, RedirectAttributes redirectAttributes) {
        log.info("{}.deleteExerciseHistory Start!", getClass().getName());
        boolean success = historyService.deleteExerciseHistory(id, user);
        if (success) {
            redirectAttributes.addFlashAttribute("toastMsg", "운동 기록이 삭제되었습니다.");
        } else {
            redirectAttributes.addFlashAttribute("toastMsg", "기록 삭제에 실패했습니다.");
        }
        log.info("{}.deleteExerciseHistory End!", getClass().getName());
        return "redirect:/history/view";
    }

    /**
     * 식단 기록 삭제 처리
     */
    @PostMapping("/diet/delete/{id}")
    public String deleteDietHistory(@PathVariable String id, @AuthenticationPrincipal UserAuthDTO user, RedirectAttributes redirectAttributes) {
        log.info("{}.deleteDietHistory Start!", getClass().getName());
        boolean success = historyService.deleteDietHistory(id, user);
        if (success) {
            redirectAttributes.addFlashAttribute("toastMsg", "식단 기록이 삭제되었습니다.");
        } else {
            redirectAttributes.addFlashAttribute("toastMsg", "기록 삭제에 실패했습니다.");
        }
        log.info("{}.deleteDietHistory End!", getClass().getName());
        return "redirect:/history/view";
    }

    /**
     *  AI 리포트 기록 삭제 처리
     */
    @PostMapping("/report/delete/{id}")
    public String deleteReportHistory(@PathVariable String id, @AuthenticationPrincipal UserAuthDTO user, RedirectAttributes redirectAttributes) {
        log.info("{}.deleteReportHistory Start!", getClass().getName());

        // 서비스의 deleteReportHistory 메서드를 호출
        boolean success = historyService.deleteReportHistory(id, user);

        if (success) {
            // 성공 시, 리다이렉트된 페이지에 토스트 메시지를 전달
            redirectAttributes.addFlashAttribute("toastMsg", "AI 리포트가 삭제되었습니다.");
        } else {
            redirectAttributes.addFlashAttribute("toastMsg", "기록 삭제에 실패했습니다.");
        }

        log.info("{}.deleteReportHistory End!", getClass().getName());
        return "redirect:/history/view"; // 내 기록 보기 목록 페이지로 리다이렉트
    }
}