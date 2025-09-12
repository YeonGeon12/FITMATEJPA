package kopo.fitmate.controller.user;

import kopo.fitmate.dto.user.UserAuthDTO;
import kopo.fitmate.dto.user.UserInfoDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 마이페이지 관련 요청을 처리하는 컨트롤러
 */
@Slf4j
@Controller
@RequestMapping("/user")
@RequiredArgsConstructor
public class MyPageController {

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
}
