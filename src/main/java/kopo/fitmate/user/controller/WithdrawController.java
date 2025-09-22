package kopo.fitmate.user.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Slf4j
@Controller
@RequestMapping("/user")
@RequiredArgsConstructor
public class WithdrawController {

    /**
     * 회원 탈퇴 폼 페이지로 이동
     */
    @GetMapping("/withdrawForm")
    public String withdrawForm() {
        log.info(this.getClass().getName() + ".withdrawForm Start!");
        return "user/withdrawForm"; // templates/user/withdrawForm.html 호출
    }
}
