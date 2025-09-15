package kopo.fitmate.controller.user;

import jakarta.validation.Valid;
import kopo.fitmate.dto.user.UpdateProfileDTO;
import kopo.fitmate.dto.user.UserAuthDTO;
import kopo.fitmate.dto.user.UserProfileDTO;
import kopo.fitmate.service.IUserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

@Slf4j
@Controller
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserProfileController {

    private final IUserService userService;

    /**
     * 나의 신체 정보 수정 폼 페이지로 이동
     */
    @GetMapping("/profileForm")
    public String profileForm(@AuthenticationPrincipal UserAuthDTO user, Model model) {
        log.info(this.getClass().getName() + ".profileForm Start!");

        // 현재 로그인된 사용자의 프로필 정보를 조회
        Optional<UserProfileDTO> profileDTOOptional = userService.getUserProfile(user.getUserNo());

        // 모델에 데이터를 담아 뷰로 전달
        if (profileDTOOptional.isPresent()) {
            // 정보가 있으면 해당 정보를 모델에 추가
            model.addAttribute("updateProfileDTO", profileDTOOptional.get());
        } else {
            // 정보가 없으면 빈 DTO 객체를 모델에 추가
            model.addAttribute("updateProfileDTO", new UpdateProfileDTO());
        }

        log.info(this.getClass().getName() + ".profileForm End!");
        return "user/profileForm"; // templates/user/profileForm.html
    }

    /**
     * 신체 정보 수정 로직 처리
     */
    @PostMapping("/updateProfile")
    public String updateProfile(@Valid @ModelAttribute UpdateProfileDTO pDTO,
                                BindingResult bindingResult,
                                @AuthenticationPrincipal UserAuthDTO user,
                                RedirectAttributes rttr) {
        log.info(this.getClass().getName() + ".updateProfile Start!");

        if (bindingResult.hasErrors()) {
            // 폼 유효성 검사 실패 시, 다시 폼으로 돌아감
            // 이 경우, Thymeleaf가 오류 메시지를 자동으로 표시해 줌
            return "user/profileForm";
        }

        try {
            // 서비스 계층에 프로필 정보 저장/수정 요청
            userService.saveOrUpdateUserProfile(pDTO, user.getUserNo());
            rttr.addFlashAttribute("successMsg", "프로필 정보가 성공적으로 저장되었습니다.");

        } catch (Exception e) {
            log.error("Profile update failed: ", e);
            rttr.addFlashAttribute("errorMsg", "정보 저장 중 오류가 발생했습니다.");
        }

        log.info(this.getClass().getName() + ".updateProfile End!");
        return "redirect:/user/mypage"; // 처리 후 마이페이지로 리다이렉트
    }
}
