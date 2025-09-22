package kopo.fitmate.user.controller; // 패키지 경로를 user로 수정

import jakarta.validation.Valid;
import kopo.fitmate.global.dto.MsgDTO;
import kopo.fitmate.user.service.IUserService;
import kopo.fitmate.user.dto.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

/**
 * 사용자 관련 비동기(AJAX) 요청을 처리하는 API 컨트롤러.
 * JoinController와 같은 패키지에 위치합니다.
 */
@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/user") // API 호출 경로는 /api/user로 유지하여 역할을 명확히 함
public class UserApiController {

    // 사용자 서비스 의존성 주입
    private final IUserService userService;

    // Spring Security의 인증 처리를 위해 AuthenticationManager를 주입받습니다.
    private final AuthenticationManager authenticationManager;

    /**
     * 이메일 중복 확인 API
     * @param email 클라이언트로부터 받은 확인할 이메일
     * @return 처리 결과를 담은 MsgDTO
     */
    @GetMapping("/check-email")
    public ResponseEntity<MsgDTO> checkEmail(@RequestParam("email") String email) {
        final String methodName = new Object() {}.getClass().getEnclosingMethod().getName();
        log.info("[{}] -> API 호출: 이메일 중복 확인. 이메일: {}", methodName, email);

        MsgDTO response = new MsgDTO();

        try {
            if (userService.checkEmailDuplicate(email)) {
                response.setResult(0); // 0: 실패(중복)
                response.setMsg("이미 사용 중인 이메일입니다.");
            } else {
                response.setResult(1); // 1: 성공(사용 가능)
                response.setMsg("사용 가능한 이메일입니다.");
            }
            log.info("이메일 중복 확인 결과: {}", response.getMsg());
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("[{}] -> 이메일 중복 확인 중 서버 오류 발생", methodName, e);
            response.setResult(-1); // -1: 서버 오류
            response.setMsg("오류가 발생했습니다. 잠시 후 다시 시도해주세요.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * 회원가입 처리 API
     * @param pDTO 클라이언트로부터 받은 회원가입 정보 (JSON)
     * @return 처리 결과를 담은 MsgDTO
     */
    @PostMapping("/join")
    public ResponseEntity<MsgDTO> doJoin(@Valid @RequestBody JoinDTO pDTO) {
        final String methodName = new Object() {}.getClass().getEnclosingMethod().getName();
        log.info("[{}] -> API 호출: 회원가입 처리 시작", methodName);
        MsgDTO response = new MsgDTO();

        try {
            userService.insertUserInfo(pDTO);

            log.info("회원가입 성공. 이메일: {}", pDTO.getEmail());
            response.setResult(1);
            response.setMsg("회원가입이 완료되었습니다. 로그인 페이지로 이동합니다.");
            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e) {
            // 서비스 단에서 의도적으로 발생시킨 예외 (비밀번호 불일치, 이메일 중복 등)
            log.warn("[{}] -> 비즈니스 로직 오류 발생: {}", methodName, e.getMessage());
            response.setResult(0);
            response.setMsg(e.getMessage()); // 서비스에서 보낸 메시지를 그대로 클라이언트에 전달
            return ResponseEntity.badRequest().body(response);

        } catch (Exception e) {
            // 예측하지 못한 모든 서버 오류
            log.error("[{}] -> 회원가입 처리 중 심각한 서버 오류 발생", methodName, e);
            response.setResult(-1);
            response.setMsg("시스템 오류가 발생했습니다. 문제가 지속되면 관리자에게 문의해주세요.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * 로그인 처리 API
     * @param pDTO 로그인 정보 (JSON)
     * @return 처리 결과를 담은 MsgDTO
     */
    @PostMapping("/login")
    public ResponseEntity<MsgDTO> doLogin(@Valid @RequestBody LoginDTO pDTO) {
        log.info("API 호출: 로그인 처리 시작");
        MsgDTO response = new MsgDTO();

        try {
            // 1. Spring Security를 이용해 사용자 인증 시도
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(pDTO.getEmail(), pDTO.getPassword())
            );

            // 2. 인증 성공 시, SecurityContext에 인증 정보 저장
            SecurityContextHolder.getContext().setAuthentication(authentication);

            log.info("로그인 성공. 이메일: {}", pDTO.getEmail());
            response.setResult(1);
            response.setMsg("로그인에 성공했습니다. 메인 페이지로 이동합니다.");
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            // 3. 인증 실패 시 (아이디/비밀번호 불일치 등)
            log.warn("로그인 실패: {}", e.getMessage());
            response.setResult(0);
            response.setMsg("아이디(이메일) 또는 비밀번호가 일치하지 않습니다.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
    }

    /**
     * 비밀번호 변경 처리 API
     * @param pDTO 클라이언트로부터 받은 비밀번호 변경 정보 (JSON)
     * @param authentication 현재 로그인된 사용자 정보
     * @return 처리 결과를 담은 MsgDTO
     */
    @PostMapping("/change-password")
    public ResponseEntity<MsgDTO> changePassword(@Valid @RequestBody ChangePasswordDTO pDTO, Authentication authentication) {
        log.info(this.getClass().getName() + ".changePassword API Start!");
        MsgDTO response = new MsgDTO();

        try {
            // 현재 로그인된 사용자의 이메일(ID) 가져오기
            String email = authentication.getName();

            // 서비스에 DTO와 이메일을 전달하여 비밀번호 변경 로직 수행
            userService.changeUserPassword(pDTO, email);

            response.setResult(1);
            response.setMsg("비밀번호가 성공적으로 변경되었습니다.");
            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e) {
            // 서비스에서 발생시킨 비즈니스 오류 (현재 비밀번호 불일치 등)
            log.warn("Password change failed: {}", e.getMessage());
            response.setResult(0);
            response.setMsg(e.getMessage());
            return ResponseEntity.badRequest().body(response);

        } catch (Exception e) {
            // 그 외 서버 오류
            log.error("Server error during password change", e);
            response.setResult(-1);
            response.setMsg("오류가 발생했습니다. 잠시 후 다시 시도해주세요.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * [새로 추가] 사용자 프로필 정보 수정 API
     * @param pDTO 클라이언트로부터 받은 프로필 정보 (JSON)
     * @param user 현재 로그인된 사용자 정보
     * @return 처리 결과를 담은 MsgDTO
     */
    @PostMapping("/update-profile")
    public ResponseEntity<MsgDTO> updateProfile(@Valid @RequestBody UpdateProfileDTO pDTO,
                                                @AuthenticationPrincipal UserAuthDTO user) {
        log.info(this.getClass().getName() + ".updateProfile API Start!");
        MsgDTO response = new MsgDTO();

        try {
            userService.saveOrUpdateUserProfile(pDTO, user.getUserNo());
            response.setResult(1);
            response.setMsg("프로필 정보가 성공적으로 저장되었습니다.");
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Profile update failed: ", e);
            response.setResult(-1);
            response.setMsg("정보 저장 중 오류가 발생했습니다. 다시 시도해주세요.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * 회원 탈퇴 API
     * @param pDTO 비밀번호 확인을 위한 DTO
     * @param user 현재 로그인된 사용자 정보
     * @return 처리 결과 메시지
     */
    @PostMapping("/withdraw")
    public ResponseEntity<MsgDTO> withdraw(@Valid @RequestBody WithdrawDTO pDTO,
                                           @AuthenticationPrincipal UserAuthDTO user) {
        log.info(this.getClass().getName() + ".withdraw API Start!");
        MsgDTO response = new MsgDTO();

        try {
            // 1. 서비스에 탈퇴 요청
            userService.deleteUser(user.getUserNo(), pDTO.getPassword());

            // 2. 탈퇴 성공 시, 현재 세션(로그인 정보)을 강제로 만료시킴
            SecurityContextHolder.clearContext();

            // 3. 성공 메시지 반환
            response.setResult(1);
            response.setMsg("회원 탈퇴가 완료되었습니다. 이용해주셔서 감사합니다.");
            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e) { // 비밀번호 불일치 등
            log.warn("Withdraw failed: " + e.getMessage());
            response.setResult(0);
            response.setMsg(e.getMessage());
            return ResponseEntity.badRequest().body(response);

        } catch (Exception e) { // 그 외 서버 오류
            log.error("Server error during withdraw", e);
            response.setResult(-1);
            response.setMsg("오류가 발생했습니다. 다시 시도해주세요.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}