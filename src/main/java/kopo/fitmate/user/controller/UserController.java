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
public class UserController {

    /**
     * 비밀번호 찾기(이메일 입력) 폼 페이지로 이동
     */
    @GetMapping("/forgotPasswordForm")
    public String forgotPasswordForm() {
        log.info(this.getClass().getName() + ".forgotPasswordForm Start!");
        return "user/forgotPasswordForm"; // templates/user/forgotPasswordForm.html
    }

    // 향후 비밀번호 재설정 처리 등 다른 사용자 관련 기능 추가 가능
}