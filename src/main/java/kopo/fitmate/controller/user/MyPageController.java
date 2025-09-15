package kopo.fitmate.controller.user;

import kopo.fitmate.dto.user.ChangePasswordDTO;
import kopo.fitmate.dto.user.UserAuthDTO;
import kopo.fitmate.dto.user.UserInfoDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Slf4j
@Controller
@RequestMapping("/user")
@RequiredArgsConstructor
public class MyPageController {

    // 비밀번호 변경 '처리' 로직이 API 컨트롤러로 이동했으므로 서비스 의존성 제거 가능
    // private final IUserService userService;

    /**
     * 마이페이지 화면으로 이동
     */
    @GetMapping("/mypage")
    public String mypage(Authentication authentication, Model model) {
        log.info(this.getClass().getName() + ".mypage Start!");
        UserAuthDTO user = (UserAuthDTO) authentication.getPrincipal();
        UserInfoDTO userInfo = UserInfoDTO.builder()
                .userName(user.getUserName())
                .email(user.getEmail())
                .build();
        model.addAttribute("userInfo", userInfo);
        log.info(this.getClass().getName() + ".mypage End!");
        return "user/mypage";
    }

    /**
     * 비밀번호 변경 폼 페이지로 이동
     */
    @GetMapping("/changePasswordForm")
    public String changePasswordForm(Model model) {
        log.info(this.getClass().getName() + ".changePasswordForm Start!");
        model.addAttribute("changePasswordDTO", new ChangePasswordDTO());
        log.info(this.getClass().getName() + ".changePasswordForm End!");
        return "user/changePasswordForm";
    }
}