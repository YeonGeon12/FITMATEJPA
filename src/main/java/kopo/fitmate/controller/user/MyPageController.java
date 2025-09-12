package kopo.fitmate.controller.user;

import jakarta.validation.Valid;
import kopo.fitmate.dto.user.ChangePasswordDTO;
import kopo.fitmate.dto.user.UserAuthDTO;
import kopo.fitmate.dto.user.UserInfoDTO;
import kopo.fitmate.service.IUserService;
import kopo.fitmate.service.impl.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * 마이페이지 관련 요청을 처리하는 컨트롤러
 */
@Slf4j
@Controller
@RequestMapping("/user")
@RequiredArgsConstructor
public class MyPageController {

    private final IUserService userService; // 서비스 의존성 주입

    /**
     * 마이페이지 화면으로 이동
     * @param authentication Spring Security가 관리하는 현재 로그인된 사용자 정보
     * @param model View로 데이터를 전달하기 위한 객체
     * @return 마이페이지 HTML 파일 경로
     */
    @GetMapping("/mypage")
    public String mypage(Authentication authentication, Model model) {
        log.info(this.getClass().getName() + ".mypage Start!");

        // [수정된 부분] (UserAuthDTO)를 앞에 추가하여 명시적으로 타입을 변환합니다.
        UserAuthDTO user = (UserAuthDTO) authentication.getPrincipal();

        // 2. 화면에 보여줄 정보(이름, 이메일)만 담는 UserInfoDTO를 생성
        UserInfoDTO userInfo = UserInfoDTO.builder()
                .userName(user.getUserName()) // 이제 에러 없이 메소드를 호출할 수 있습니다.
                .email(user.getEmail())
                .build();

        // 3. Model 객체에 UserInfoDTO를 담아 View로 전달
        model.addAttribute("userInfo", userInfo);

        log.info(this.getClass().getName() + ".mypage End!");

        // 4. resources/templates/user/mypage.html 파일을 화면에 보여줌
        return "user/mypage";
    }


    /**
     * [새로 추가] 비밀번호 변경 폼 페이지로 이동
     */
    @GetMapping("/changePasswordForm")
    public String changePasswordForm(Model model) {
        log.info(this.getClass().getName() + ".changePasswordForm Start!");

        // 폼과 데이터를 주고받을 DTO 객체를 모델에 추가
        model.addAttribute("changePasswordDTO", new ChangePasswordDTO());

        log.info(this.getClass().getName() + ".changePasswordForm End!");
        return "user/changePasswordForm"; // templates/user/changePasswordForm.html 호출
    }

    /**
     * [새로 추가] 비밀번호 변경 로직 처리
     */
    @PostMapping("/changePassword")
    public String changePassword(@Valid @ModelAttribute ChangePasswordDTO pDTO,
                                 BindingResult bindingResult,
                                 Authentication authentication,
                                 RedirectAttributes rttr) {
        log.info(this.getClass().getName() + ".changePassword Start!");

        // 1. 폼 데이터 유효성 검사 (DTO에 설정한 @NotBlank, @Pattern 등)
        if (bindingResult.hasErrors()) {
            return "user/changePasswordForm"; // 유효성 검사 실패 시, 다시 폼으로 돌아감
        }

        try {
            // 2. 현재 로그인된 사용자의 이메일(ID) 가져오기
            UserAuthDTO user = (UserAuthDTO) authentication.getPrincipal();
            String email = user.getEmail();

            // 3. 서비스에 DTO와 이메일을 전달하여 비밀번호 변경 로직 수행
            userService.changeUserPassword(pDTO, email);

            // 4. 성공 시, 메시지와 함께 마이페이지로 리다이렉트
            rttr.addFlashAttribute("successMsg", "비밀번호가 성공적으로 변경되었습니다.");
            return "redirect:/user/mypage";

        } catch (IllegalArgumentException e) {
            // 5. 서비스에서 발생시킨 비즈니스 오류 처리 (비밀번호 불일치 등)
            log.warn("Password change failed: " + e.getMessage());
            rttr.addFlashAttribute("errorMsg", e.getMessage()); // 오류 메시지를 폼으로 전달
            return "redirect:/user/changePasswordForm";

        } catch (Exception e) {
            // 6. 그 외 서버 오류 처리
            log.error("Server error during password change", e);
            rttr.addFlashAttribute("errorMsg", "오류가 발생했습니다. 다시 시도해주세요.");
            return "redirect:/user/changePasswordForm";
        }
    }
}
