package kopo.fitmate.user.controller;

import kopo.fitmate.user.service.IUserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Slf4j
@Controller
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final IUserService userService;

    /**
     * 비밀번호 찾기(이메일 입력) 폼 페이지로 이동
     */
    @GetMapping("/forgotPasswordForm")
    public String forgotPasswordForm() {
        log.info(this.getClass().getName() + ".forgotPasswordForm Start!");
        return "user/forgotPasswordForm"; // templates/user/forgotPasswordForm.html
    }

    /**
     * 이메일 링크를 통해 새 비밀번호 설정 폼 페이지로 이동
     */
    @GetMapping("/resetPassword")
    public String resetPasswordForm(@RequestParam("token") String token, Model model) {
        log.info(this.getClass().getName() + ".비밀번호 재설정 시작!");

        // 1. 토큰이 유효한지 서비스 레이어에서 검증
        if (!userService.validatePasswordResetToken(token)) {
            log.warn("유효하지 않거나 만료된 토큰으로 접근 시도: {}", token);
            model.addAttribute("errorMsg", "링크가 만료되었거나 유효하지 않습니다. 다시 요청해주세요.");
            return "user/forgotPasswordForm"; // 유효하지 않으면 다시 이메일 입력 페이지로 보냄
        }

        // 2. 토큰이 유효하면, 모델에 토큰을 담아 재설정 페이지로 이동
        model.addAttribute("token", token);

        log.info(this.getClass().getName() + ".비밀번호 재설정 끝!");
        return "user/resetPasswordForm"; // templates/user/resetPasswordForm.html
    }
}