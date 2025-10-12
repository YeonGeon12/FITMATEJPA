package kopo.fitmate.history.controller;

import kopo.fitmate.history.dto.HistoryViewDTO;
import kopo.fitmate.history.service.IHistoryService;
import kopo.fitmate.user.dto.UserAuthDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * '내 기록 보기' 기능과 관련된 웹 요청을 처리하는 컨트롤러입니다.
 */
@Slf4j
@Controller
@RequestMapping("/history")
@RequiredArgsConstructor
public class HistoryController {

    // 이전에 생성한 HistoryService를 주입받습니다.
    private final IHistoryService historyService;

    /**
     * '내 기록 보기' 페이지로 이동하는 메서드이다.
     * GET 방식으로 /history/view 요청을 처리한다.
     *
     * @param user  현재 로그인된 사용자의 정보 (Spring Security가 자동으로 주입해 줍니다)
     * @param model View(HTML)에 데이터를 전달하기 위한 객체
     * @return 보여줄 뷰의 이름 (templates/history/historyView.html)
     */
    @GetMapping("/view")
    public String historyView(@AuthenticationPrincipal UserAuthDTO user, Model model) {
        log.info("{}.historyView Start!", getClass().getName());

        // 1. Service를 호출하여 현재 로그인한 사용자의 모든 기록(운동, 식단)을 가져옵니다.
        HistoryViewDTO historyData = historyService.getHistoryList(user);

        // 2. 가져온 데이터 묶음(HistoryViewDTO)을 'historyData'라는 이름으로 Model에 추가합니다.
        //    이제 HTML 파일에서 'historyData'라는 이름으로 이 데이터에 접근할 수 있습니다.
        model.addAttribute("historyData", historyData);

        log.info("{}.historyView End!", getClass().getName());

        // 3. 사용자에게 'history/historyView.html' 파일을 보여줍니다.
        return "history/historyView";
    }
}