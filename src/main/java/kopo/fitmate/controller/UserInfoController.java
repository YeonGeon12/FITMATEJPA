package kopo.fitmate.controller;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import kopo.fitmate.dto.user.UserFindIdDTO;
import kopo.fitmate.dto.user.UserJoinDTO;
import kopo.fitmate.dto.user.UserLoginDTO;
import kopo.fitmate.dto.user.UserEmailAuthDTO;
import kopo.fitmate.repository.impl.UserInfoEntity;
import kopo.fitmate.service.IUserInfoService;
import kopo.fitmate.service.IMailService;
import kopo.fitmate.util.EncryptUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

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
        // 통합 구조라 purpose는 여기선 아직 필요 없음
        return mailService.sendAuthCode(email, session); // 인증번호는 EMAIL_AUTH_CODE에 저장
    }

    /**
     * 인증번호 확인 및 목적별 세션 저장 처리
     */
    @PostMapping("/verify-auth-code")
    @ResponseBody
    public boolean verifyAuthCode(@RequestBody UserEmailAuthDTO dto,
                                  HttpSession session) {

        String inputCode = dto.getAuthCode(); // 사용자가 입력한 인증번호
        String savedCode = (String) session.getAttribute("EMAIL_AUTH_CODE"); // 세션에 저장된 인증번호

        if (inputCode != null && inputCode.equals(savedCode)) {

            switch (dto.getPurpose()) {
                case "signup":
                    session.setAttribute("EMAIL_AUTH_SIGNUP", true);
                    break;

                case "findId":
                    session.setAttribute("FIND_ID_NAME", dto.getUserName());
                    session.setAttribute("FIND_ID_EMAIL", dto.getEmail());
                    break;

                case "findPw":
                    session.setAttribute("FIND_PW_NAME", dto.getUserName());
                    session.setAttribute("FIND_PW_EMAIL", dto.getEmail());
                    break;

                default:
                    return false; // 예상하지 못한 purpose가 들어온 경우
            }

            return true; // 인증 성공 및 세션 저장 완료
        }

        return false; // 인증 실패
    }

    /**
     * 아이디 찾기 화면 출력
     */
    @GetMapping("/find-id")
    public String showFindIdForm(Model model) {
        model.addAttribute("user", new UserFindIdDTO());
        return "user/find-id"; // templates/user/find-id.html
    }


    /**
     * 이름과 이메일로 아이디 찾기 요청 처리
     *
     * @param dto 사용자 이름과 이메일 정보를 담은 DTO
     * @return 성공 시 아이디, 이름, 복호화된 이메일 정보 반환 / 실패 시 404 에러 응답
     */
    @PostMapping("/find-id")
    @ResponseBody
    public ResponseEntity<?> findUserId(@RequestBody @Valid UserFindIdDTO dto) throws Exception {
        Optional<UserInfoEntity> userOpt = userInfoService.findUserIdByNameAndEmail(dto);

        if (userOpt.isPresent()) {
            UserInfoEntity user = userOpt.get();

            Map<String, String> result = new HashMap<>();
            result.put("userId", user.getUserId());
            result.put("userName", user.getUserName());
            result.put("email", EncryptUtil.decAES128CBC(user.getEmail())); // 복호화된 이메일 반환

            return ResponseEntity.ok(result);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("해당 정보로 가입된 사용자가 없습니다.");
        }
    }

    /**
     * 사용자가 입력한 인증번호를 세션의 값과 비교하여 인증 여부를 확인
     *
     * @param authCode 사용자가 입력한 인증번호
     * @param session 세션에 저장된 인증번호 비교용
     * @return 인증 성공 여부 (true / false)
     */
    @PostMapping("/find-id/verify-auth-code")
    @ResponseBody
    public boolean verifyFindIdAuthCode(@RequestParam("authCode") String authCode, HttpSession session) {

        // 세션에 저장된 인증번호 불러오기
        String sessionCode = (String) session.getAttribute("EMAIL_AUTH_CODE");

        // 비교 후 결과 반환
        return authCode != null && authCode.equals(sessionCode);
    }

    /**
     * 세션에 저장된 이름과 이메일 정보를 기반으로 사용자 아이디 조회
     *
     * @param session 인증 성공 후 저장된 이름 + 이메일 정보
     * @return 사용자 ID, 이름, 복호화된 이메일 정보를 JSON으로 반환
     */
    @GetMapping("/find-id/result")
    @ResponseBody
    public ResponseEntity<?> getUserIdFromSession(HttpSession session) throws Exception {

        // 1. 세션에서 이름과 이메일 정보 가져오기
        String userName = (String) session.getAttribute("FIND_ID_NAME");
        String email = (String) session.getAttribute("FIND_ID_EMAIL");

        // 2. 값이 비어 있으면 잘못된 접근
        if (userName == null || email == null) {
            return ResponseEntity.badRequest().body("인증 세션이 만료되었습니다.");
        }

        // 3. 사용자 조회
        UserFindIdDTO dto = new UserFindIdDTO();
        dto.setUserName(userName);
        dto.setEmail(email);

        Optional<UserInfoEntity> userOpt = userInfoService.findUserIdByNameAndEmail(dto);

        if (userOpt.isPresent()) {
            UserInfoEntity user = userOpt.get();

            Map<String, String> result = new HashMap<>();
            result.put("userId", user.getUserId());
            result.put("userName", user.getUserName());
            result.put("email", EncryptUtil.decAES128CBC(user.getEmail())); // 복호화된 이메일

            return ResponseEntity.ok(result);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("사용자를 찾을 수 없습니다.");
        }
    }

    /**
     * 아이디 찾기용 이름 + 이메일을 세션에 저장
     */
    @PostMapping("/store-find-id-session")
    @ResponseBody
    public void storeFindIdSession(@RequestParam("userName") String userName,
                                   @RequestParam("email") String email,
                                   HttpSession session) {
        session.setAttribute("FIND_ID_NAME", userName);
        session.setAttribute("FIND_ID_EMAIL", email);
    }

    @PostMapping("/user/send-auth-code")
    public String sendAuthCode(@RequestParam String email) {
        String code = MailUtil.sendAuthCode(email);
        session.setAttribute("authCode", code);
        return "ok";
    }

    @PostMapping("/user/store-find-id-session")
    public String storeFindIdInfo(@RequestParam String userName, @RequestParam String email) {
        session.setAttribute("findIdName", userName);
        session.setAttribute("findIdEmail", email);
        return "ok";
    }

    @PostMapping("/user/verify-auth-code")
    public String verifyAuthCode(@RequestParam String authCode) {
        String sessionCode = (String) session.getAttribute("authCode");
        if (authCode.equals(sessionCode)) {
            return "redirect:/user/find-id-result";
        } else {
            model.addAttribute("error", "invalid_code");
            return "verify-id-code";
        }
    }

    @GetMapping("/user/find-id-result")
    public String findIdResult(Model model) {
        String name = (String) session.getAttribute("findIdName");
        String email = (String) session.getAttribute("findIdEmail");
        Optional<UserEntity> user = userRepository.findByNameAndEmail(name, email);
        user.ifPresent(u -> model.addAttribute("userId", u.getUserId()));
        return "find-id-result";
    }

}
