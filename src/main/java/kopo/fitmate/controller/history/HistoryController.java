package kopo.fitmate.controller.history;

import kopo.fitmate.dto.history.AiReportHistoryDTO;
import kopo.fitmate.dto.history.DietHistoryDTO;
import kopo.fitmate.dto.history.ExerciseHistoryDTO;
import kopo.fitmate.dto.user.UserAuthDTO;
import kopo.fitmate.repository.mongo.entity.AiReportEntity;
import kopo.fitmate.repository.mongo.entity.DietInfoEntity;
import kopo.fitmate.repository.mongo.entity.ExerciseInfoEntity;
import kopo.fitmate.service.IHistoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.Objects;

@Slf4j
@Controller
@RequestMapping("/history")
@RequiredArgsConstructor
public class HistoryController {

    private final IHistoryService historyService;

    /**
     * 내 기록 보기 페이지로 이동
     */
    @GetMapping("")
    public String historyView(@AuthenticationPrincipal UserAuthDTO user, Model model) {
        log.info(this.getClass().getName() + ".historyView Start!");

        Long userNo = user.getUserNo();

        // 각 서비스 메서드를 호출하여 데이터 조회
        List<ExerciseHistoryDTO> exerciseList = historyService.getExerciseHistory(userNo);
        List<DietHistoryDTO> dietList = historyService.getDietHistory(userNo);
        List<AiReportHistoryDTO> reportList = historyService.getAiReportHistory(userNo);

        // 조회된 데이터를 모델에 추가
        model.addAttribute("exerciseList", exerciseList);
        model.addAttribute("dietList", dietList);
        model.addAttribute("reportList", reportList);

        log.info(this.getClass().getName() + ".historyView End!");
        return "history/historyView"; // templates/history/historyView.html 렌더링
    }

    // --- [추가] 상세보기 컨트롤러 ---

    @GetMapping("/exercise/{id}")
    public String exerciseDetail(@PathVariable String id, @AuthenticationPrincipal UserAuthDTO user, Model model) {
        ExerciseInfoEntity detail = historyService.getExerciseDetail(id);

        // 보안 체크: 조회한 기록이 현재 로그인한 사용자의 것인지 확인
        if (detail != null && Objects.equals(detail.getUserId(), user.getUserNo())) {
            model.addAttribute("detail", detail);
        }

        return "history/exerciseDetail"; // 상세 페이지 HTML로 연결
    }

    @GetMapping("/diet/{id}")
    public String dietDetail(@PathVariable String id, @AuthenticationPrincipal UserAuthDTO user, Model model) {
        DietInfoEntity detail = historyService.getDietDetail(id);

        // 보안 체크
        if (detail != null && Objects.equals(detail.getUserId(), user.getUserNo())) {
            model.addAttribute("detail", detail);
        }

        return "history/dietDetail";
    }

    @GetMapping("/report/{id}")
    public String reportDetail(@PathVariable String id, @AuthenticationPrincipal UserAuthDTO user, Model model) {
        AiReportEntity detail = historyService.getAiReportDetail(id);

        // 보안 체크
        if (detail != null && Objects.equals(detail.getUserId(), user.getUserNo())) {
            model.addAttribute("detail", detail);
        }

        return "history/aiReportDetail";
    }

    // --- [추가] 삭제 컨트롤러 ---

    @PostMapping("/exercise/delete/{id}")
    public String deleteExercise(@PathVariable String id, @AuthenticationPrincipal UserAuthDTO user) {
        ExerciseInfoEntity detail = historyService.getExerciseDetail(id);

        // 보안 체크: 삭제하려는 기록이 본인 소유인지 한 번 더 확인
        if (detail != null && Objects.equals(detail.getUserId(), user.getUserNo())) {
            historyService.deleteExerciseHistory(id);
        }

        return "redirect:/history"; // 삭제 후 목록 페이지로 이동
    }

    @PostMapping("/diet/delete/{id}")
    public String deleteDiet(@PathVariable String id, @AuthenticationPrincipal UserAuthDTO user) {
        DietInfoEntity detail = historyService.getDietDetail(id);

        // 보안 체크
        if (detail != null && Objects.equals(detail.getUserId(), user.getUserNo())) {
            historyService.deleteDietHistory(id);
        }

        return "redirect:/history";
    }

    @PostMapping("/report/delete/{id}")
    public String deleteReport(@PathVariable String id, @AuthenticationPrincipal UserAuthDTO user) {
        AiReportEntity detail = historyService.getAiReportDetail(id);

        // 보안 체크
        if (detail != null && Objects.equals(detail.getUserId(), user.getUserNo())) {
            historyService.deleteAiReportHistory(id);
        }

        return "redirect:/history";
    }
}
