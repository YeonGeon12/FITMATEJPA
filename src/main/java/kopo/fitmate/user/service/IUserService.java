package kopo.fitmate.user.service;

import kopo.fitmate.user.dto.ChangePasswordDTO;
import kopo.fitmate.user.dto.JoinDTO;
import kopo.fitmate.user.dto.UpdateProfileDTO;
import kopo.fitmate.user.dto.UserProfileDTO;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.Optional;

/**
 * 사용자 관련 비즈니스 로직을 정의하는 인터페이스.
 * Spring Security의 UserDetailsService를 상속받아 로그인 기능을 통합합니다.
 */
public interface IUserService extends UserDetailsService {

    /**
     * 회원 가입 (사용자 정보 등록)
     * @param pDTO 회원가입 정보를 담은 DTO
     */
    void insertUserInfo(JoinDTO pDTO) throws Exception;

    /**
     * 이메일 중복 확인
     * @param email 중복 확인할 이메일 주소
     * @return 중복이면 true, 아니면 false
     */
    boolean checkEmailDuplicate(String email);

    /**
     * 이메일로 사용자를 찾아 비밀번호 재설정 토큰을 생성하고 DB에 저장
     * @param email 사용자의 이메일
     * @return 생성된 토큰 문자열
     */
    String createPasswordResetTokenForUser(String email) throws Exception;

    /**
     * 사용자 이메일로 비밀번호 재설정 링크가 포함된 이메일을 발송하는 로직
     * @param email 수신자 이메일 주소
     * @param token 재설정 토큰
     */
    void sendPasswordResetEmail(String email, String token) throws Exception;

    /**
     * 제공된 토큰이 유효한지(존재하는지, 만료되지 않았는지) 검증하는 로직
     * @param token 검증할 토큰
     * @return 유효하면 true, 아니면 false
     */
    boolean validatePasswordResetToken(String token);

    /**
     * 토큰을 사용하여 확인된 사용자의 비밀번호를 실제로 재설정하는 로직
     * @param token 유효성이 검증된 토큰
     * @param newPassword 사용자가 새로 입력한 비밀번호
     */
    void resetPassword(String token, String newPassword) throws Exception;


    /**
     * 로그인된 사용자의 비밀번호 변경
     * @param pDTO 변경할 비밀번호 정보가 담긴 DTO
     * @param email 현재 로그인된 사용자의 이메일
     */
    void changeUserPassword(ChangePasswordDTO pDTO, String email) throws Exception;

    Optional<UserProfileDTO> getUserProfile(Long userNo);

    /**
     * 사용자 프로필 정보 저장 또는 업데이트
     * @param pDTO 저장/업데이트할 프로필 정보
     * @param userNo 현재 로그인된 사용자의 고유 번호
     */
    void saveOrUpdateUserProfile(UpdateProfileDTO pDTO, Long userNo) throws Exception;

    /**
     * 회원 탈퇴
     * @param userNo 탈퇴할 사용자의 고유 번호
     * @param password 비밀번호 확인
     */
    void deleteUser(Long userNo, String password) throws Exception;

}