package kopo.fitmate.controller.user;

import kopo.fitmate.dto.user.UpdateProfileDTO;
import kopo.fitmate.dto.user.UserAuthDTO;
import kopo.fitmate.dto.user.UserProfileDTO;
import kopo.fitmate.service.IUserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Optional;

@Slf4j
@Controller
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserProfileController {

    private final IUserService userService;

    /**
     * 나의 신체 정보 수정 폼 페이지로 이동
     * 기존에 저장된 정보가 있다면 DTO에 담아서 전달합니다.
     */
    @GetMapping("/profileForm")
    public String profileForm(@AuthenticationPrincipal UserAuthDTO user, Model model) {
        log.info(this.getClass().getName() + ".profileForm Start!");

        Optional<UserProfileDTO> profileDTOOptional = userService.getUserProfile(user.getUserNo());

        // [수정됨] 모델에 담는 DTO의 이름을 updateProfileDTO로 통일합니다.
        // th:object와 매핑하기 위함입니다.
        if (profileDTOOptional.isPresent()) {
            model.addAttribute("updateProfileDTO", profileDTOOptional.get());
        } else {
            model.addAttribute("updateProfileDTO", new UpdateProfileDTO());
        }

        log.info(this.getClass().getName() + ".profileForm End!");
        return "user/profileForm";
    }

    // POST 방식의 updateProfile 메서드는 UserApiController로 이전했으므로 삭제하기
}