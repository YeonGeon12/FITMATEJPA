package kopo.fitmate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Slf4j
@Controller
public class IndexController {

    /**
     * 프로젝트의 첫 진입점(루트 경로)을 처리하는 메서드.
     * 로그인 상태에 따라 적절한 페이지로 리다이렉트합니다.
     */
    @GetMapping("/")
    public String root() {
        log.info(this.getClass().getName() + ".root Start!");

        // 현재 사용자의 인증 정보를 가져옴
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        // 인증 정보가 존재하고, 사용자가 '익명 사용자'가 아닐 경우 (즉, 로그인 상태일 경우)
        if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getPrincipal())) {
            log.info("인증된 사용자, /index로 리다이렉트");
            return "redirect:/index";
        }

        // 비로그인 상태일 경우, 로그인 페이지로 리다이렉트
        log.info("비인증 사용자, /user/loginForm으로 리다이렉트");
        return "redirect:/user/loginForm";
    }

    /**
     * 로그인한 사용자만 접근할 수 있는 실제 메인 페이지를 보여주는 메서드.
     */
    @GetMapping("/index")
    public String indexView() {
        log.info(this.getClass().getName() + ".indexView Start!");

        // templates 폴더 아래의 index.html을 찾아 화면에 보여줍니다.
        return "index";
    }
}