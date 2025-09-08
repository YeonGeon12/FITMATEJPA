package kopo.fitmate.controller;

import jakarta.validation.Valid;
import kopo.fitmate.dto.UserDTO;
import kopo.fitmate.service.IUserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Slf4j
@RequiredArgsConstructor
@Controller
@RequestMapping("/user")
public class UserController {

    private final IUserService userService;

    // 회원가입 페이지 이동
    @GetMapping("/userRegForm")
    public String userRegForm(Model model) {
        log.info(this.getClass().getName() + ".userRegForm !");
        // DTO 객체를 모델에 추가하여 Thymeleaf에서 사용
        model.addAttribute("userDTO", new UserDTO());
        return "user/userRegForm"; // templates/user/userRegForm.html
    }

    // 회원가입 로직 처리
    @PostMapping("/insertUserInfo")
    public String insertUserInfo(@Valid @ModelAttribute UserDTO pDTO, BindingResult bindingResult, Model model) {
        log.info(this.getClass().getName() + ".insertUserInfo Start!");

        if (bindingResult.hasErrors()) {
            // 유효성 검사 에러가 있으면, 다시 폼으로 돌려보냄
            return "user/userRegForm";
        }

        try {
            userService.insertUserInfo(pDTO);

        } catch (RuntimeException e) {
            log.warn(e.getMessage());
            // 서비스에서 발생한 예외(중복, 비밀번호 불일치) 처리
            model.addAttribute("errorMessage", e.getMessage());
            return "user/userRegForm";

        } catch (Exception e) {
            log.error("회원가입 처리 중 오류 발생", e);
            model.addAttribute("errorMessage", "처리 중 오류가 발생했습니다.");
            return "user/userRegForm";
        }

        // 회원가입 성공 시, 로그인 페이지로 리다이렉트
        return "redirect:/user/loginForm";
    }
}