package kopo.fitmate.controller.user;

import jakarta.validation.Valid;
import kopo.fitmate.dto.user.JoinDTO;
import kopo.fitmate.service.IUserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping; // RequestMapping 임포트
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Slf4j
@Controller
@RequestMapping("/user") // "/user" 경로를 이 컨트롤러가 담당하도록 명시
@RequiredArgsConstructor
public class JoinController {

    private final IUserService userService;

    /**
     * 회원가입 폼 페이지로 이동하는 메서드
     * [GET] /user/joinForm
     */
    @GetMapping("/joinForm")
    public String joinForm(Model model) {
        log.info(this.getClass().getName() + ".joinForm Start!");
        model.addAttribute("joinDTO", new JoinDTO());
        return "user/joinForm"; // "templates/user/joinForm.html"을 찾아 렌더링
    }

    /**
     * Thymeleaf 폼으로부터 회원가입 데이터를 받아 처리하는 메서드
     * [POST] /user/join
     */
    @PostMapping("/join")
    public String doJoin(@Valid @ModelAttribute("joinDTO") JoinDTO pDTO,
                         BindingResult bindingResult,
                         RedirectAttributes redirectAttributes) {

        log.info(this.getClass().getName() + ".doJoin Start!");

        if (bindingResult.hasErrors()) {
            log.warn("Form data validation failed");
            return "user/joinForm"; // 유효성 검증 실패 시, 다시 회원가입 폼으로
        }

        try {
            userService.insertUserInfo(pDTO);
        } catch (IllegalArgumentException e) {
            log.warn("Join process business error: " + e.getMessage());
            bindingResult.reject("join.fail", e.getMessage());
            return "user/joinForm";
        } catch (Exception e) {
            log.error("Join process server error", e);
            bindingResult.reject("join.fail", "시스템 오류가 발생했습니다.");
            return "user/joinForm";
        }

        log.info(this.getClass().getName() + ".doJoin End!");
        redirectAttributes.addFlashAttribute("successMsg", "회원가입이 완료되었습니다. 로그인해주세요.");
        return "redirect:/user/loginForm"; // 로그인 폼 페이지로 리다이렉트
    }
}

