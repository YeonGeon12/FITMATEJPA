package kopo.fitmate.user.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Slf4j
@Controller
@RequestMapping("/user")
public class LoginController {

    /**
     * 로그인 폼 페이지로 이동
     * SecurityConfig에 설정된 '.loginPage("/user/loginForm")' 요청을 처리합니다.
     */
    @GetMapping("/loginForm")
    public String loginForm() {
        log.info(this.getClass().getName() + ".loginForm Start!");

        // templates/user/loginForm.html 파일을 화면에 보여줍니다.
        return "user/loginForm";
    }
}