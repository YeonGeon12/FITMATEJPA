package kopo.fitmate.controller.history;

import kopo.fitmate.dto.user.UserAuthDTO;
import kopo.fitmate.service.IHistoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Objects;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Controller
@RequestMapping("/history")
public class HistoryController {

    private final IHistoryService historyService;

    /**
     * 내 기록 보기 페이지
     * [수정] HttpSession 대신 @AuthenticationPrincipal을 사용하여 로그인된 사용자 정보를 직접 주입받습니다.
     * Spring Security가 자동으로 처리해주므로 세션 유무를 직접 확인할 필요가 없습니다.
     */
    @GetMapping("")
    public String getHistory(@AuthenticationPrincipal UserAuthDTO user, Model model, @ModelAttribute("msg") String msg) {
        log.info(this.getClass().getName() + ".getHistory Start!");

        // 로그인한 사용자의 기록을 조회
        model.addAttribute("exerciseList", historyService.getExerciseHistory(user.getUserNo()));
        model.addAttribute("dietList", historyService.getDietHistory(user.getUserNo()));
        model.addAttribute("reportList", historyService.getAiReportHistory(user.getUserNo()));

        // Controller에 전달된 성공 메시지가 있다면, Model에 추가하여 View로 전달
        if (msg != null && !msg.isEmpty()) {
            model.addAttribute("toastMsg", msg);
        }

        log.info(this.getClass().getName() + ".getHistory End!");
        return "history/historyView";
    }

    /* ------------------------------ 상세 보기 ------------------------------ */

    @GetMapping("/exercise/{id}")
    public String getExerciseDetail(@PathVariable String id, @AuthenticationPrincipal UserAuthDTO user, Model model) {
        Optional.ofNullable(historyService.getExerciseDetail(id))
                .filter(detail -> Objects.equals(detail.getUserId(), user.getUserNo()))
                .ifPresent(detail -> model.addAttribute("detail", detail));

        return "history/exerciseDetail";
    }

    @GetMapping("/diet/{id}")
    public String getDietDetail(@PathVariable String id, @AuthenticationPrincipal UserAuthDTO user, Model model) {
        Optional.ofNullable(historyService.getDietDetail(id))
                .filter(detail -> Objects.equals(detail.getUserId(), user.getUserNo()))
                .ifPresent(detail -> model.addAttribute("detail", detail));

        return "history/dietDetail";
    }

    @GetMapping("/report/{id}")
    public String getAiReportDetail(@PathVariable String id, @AuthenticationPrincipal UserAuthDTO user, Model model) {
        Optional.ofNullable(historyService.getAiReportDetail(id))
                .filter(detail -> Objects.equals(detail.getUserId(), user.getUserNo()))
                .ifPresent(detail -> model.addAttribute("detail", detail));

        return "history/aiReportDetail";
    }

    /* ------------------------------ 삭제 처리 ------------------------------ */

    @PostMapping("/exercise/delete/{id}")
    public String deleteExerciseHistory(@PathVariable String id, @AuthenticationPrincipal UserAuthDTO user, RedirectAttributes redirectAttributes) {
        Optional.ofNullable(historyService.getExerciseDetail(id))
                .filter(detail -> Objects.equals(detail.getUserId(), user.getUserNo()))
                .ifPresent(detail -> {
                    historyService.deleteExerciseHistory(id);
                    redirectAttributes.addFlashAttribute("msg", "운동 기록이 성공적으로 삭제되었습니다.");
                });

        return "redirect:/history";
    }

    @PostMapping("/diet/delete/{id}")
    public String deleteDietHistory(@PathVariable String id, @AuthenticationPrincipal UserAuthDTO user, RedirectAttributes redirectAttributes) {
        Optional.ofNullable(historyService.getDietDetail(id))
                .filter(detail -> Objects.equals(detail.getUserId(), user.getUserNo()))
                .ifPresent(detail -> {
                    historyService.deleteDietHistory(id);
                    redirectAttributes.addFlashAttribute("msg", "식단 기록이 성공적으로 삭제되었습니다.");
                });

        return "redirect:/history";
    }

    @PostMapping("/report/delete/{id}")
    public String deleteAiReportHistory(@PathVariable String id, @AuthenticationPrincipal UserAuthDTO user, RedirectAttributes redirectAttributes) {
        Optional.ofNullable(historyService.getAiReportDetail(id))
                .filter(detail -> Objects.equals(detail.getUserId(), user.getUserNo()))
                .ifPresent(detail -> {
                    historyService.deleteAiReportHistory(id);
                    redirectAttributes.addFlashAttribute("msg", "AI 리포트가 성공적으로 삭제되었습니다.");
                });

        return "redirect:/history";
    }
}