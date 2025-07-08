package kopo.fitmate.controller;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import kopo.fitmate.dto.user.UserJoinDTO;
import kopo.fitmate.dto.user.UserLoginDTO;
import kopo.fitmate.dto.user.UserEmailAuthDTO;
import kopo.fitmate.service.IUserInfoService;
import kopo.fitmate.service.IMailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserInfoController {

    private final IUserInfoService userInfoService;
    private final IMailService mailService;

    /**
     * 아이디 중복 체크
     */
    @PostMapping("/checkUserId")
    @ResponseBody
    public boolean checkUserId(@RequestParam("userId") String userId) throws Exception {
        return userInfoService.isUserIdDuplicate(userId);
    }

    /**
     * 회원가입 폼
     */
    @GetMapping("/join")
    public String userJoin(Model model) {
        model.addAttribute("user", new UserJoinDTO());
        return "user/join";
    }

    /**
     * 회원가입 처리
     */
    @PostMapping("/join")
    public String registerUser(@Valid @ModelAttribute("user") UserJoinDTO dto,
                               Model model) throws Exception {
        if (userInfoService.isUserIdDuplicate(dto.getUserId())) {
            model.addAttribute("error", "이미 사용 중인 아이디입니다.");
            return "user/join";
        }

        userInfoService.registerUser(dto);
        return "redirect:/user/login";
    }

    /**
     * 로그인 폼
     */
    @GetMapping("/login")
    public String login(Model model) {
        model.addAttribute("user", new UserLoginDTO());
        return "user/login";
    }

    /**
     * 로그인 처리
     */
    @PostMapping("/login")
    public String loginUser(@ModelAttribute("user") UserLoginDTO dto,
                            Model model) throws Exception {
        boolean loginSuccess = userInfoService.loginUser(dto);

        if (!loginSuccess) {
            model.addAttribute("error", "아이디 또는 비밀번호가 올바르지 않습니다.");
            return "user/login";
        }

        return "redirect:/index";
    }

    /**
     * 인증번호 이메일 발송
     */
    @PostMapping("/send-auth-code")
    @ResponseBody
    public int sendEmailAuthCode(@RequestBody UserEmailAuthDTO dto,
                                 HttpSession session) throws Exception {

        String email = dto.getEmail(); // DTO에서 email 추출
        return mailService.sendAuthCode(email, session);
    }

    /**
     * 인증번호 확인 처리
     */
    @PostMapping("/verify-auth-code")
    @ResponseBody
    public boolean verifyAuthCode(@RequestBody UserEmailAuthDTO dto,
                                  HttpSession session) {

        String inputCode = dto.getAuthCode(); // DTO에서 인증번호 추출
        String savedCode = (String) session.getAttribute("EMAIL_AUTH_CODE");

        return inputCode.equals(savedCode);
    }
}
