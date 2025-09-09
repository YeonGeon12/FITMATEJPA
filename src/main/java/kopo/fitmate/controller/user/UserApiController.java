package kopo.fitmate.controller.user; // 패키지 경로를 user로 수정

import jakarta.validation.Valid;
import kopo.fitmate.dto.user.JoinDTO;
import kopo.fitmate.dto.user.LoginDTO;
import kopo.fitmate.dto.MsgDTO;
import kopo.fitmate.service.IUserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
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
}