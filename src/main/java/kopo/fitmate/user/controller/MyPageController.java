package kopo.fitmate.user.controller;

import kopo.fitmate.user.dto.ChangePasswordDTO;
import kopo.fitmate.user.dto.UserAuthDTO;
import kopo.fitmate.user.dto.UserInfoDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Slf4j
@Controller
@RequestMapping("/user")
@RequiredArgsConstructor
public class MyPageController {

    /**
     * 마이페이지 화면으로 이동
     */
    @GetMapping("/mypage")
    public String mypage(Authentication authentication, Model model) {
        log.info(this.getClass().getName() + ".마이페이지 시작!");
        UserAuthDTO user = (UserAuthDTO) authentication.getPrincipal();
        UserInfoDTO userInfo = UserInfoDTO.builder()
                .userName(user.getUserName())
                .email(user.getEmail())
                .build();
        model.addAttribute("userInfo", userInfo);
        log.info(this.getClass().getName() + ".마이페이지 끝!");
        return "user/mypage";
    }

    /**
     * 비밀번호 변경 폼 페이지로 이동
     */
    @GetMapping("/changePasswordForm")
    public String changePasswordForm(Model model) {
        log.info(this.getClass().getName() + ".비밀번호 변경 시작!");
        model.addAttribute("changePasswordDTO", new ChangePasswordDTO());
        log.info(this.getClass().getName() + ".비밀번호 변경 끝!");
        return "user/changePasswordForm";
    }
}