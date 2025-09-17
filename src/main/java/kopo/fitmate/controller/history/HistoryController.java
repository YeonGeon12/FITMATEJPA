package kopo.fitmate.controller.history;

import kopo.fitmate.dto.history.AiReportHistoryDTO;
import kopo.fitmate.dto.history.DietHistoryDTO;
import kopo.fitmate.dto.history.ExerciseHistoryDTO;
import kopo.fitmate.dto.user.UserAuthDTO;
import kopo.fitmate.service.IHistoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

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
}
