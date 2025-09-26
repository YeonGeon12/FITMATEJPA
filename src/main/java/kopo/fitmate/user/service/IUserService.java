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
     * 로그인된 사용자의 비밀번호 변경
     * @param pDTO 변경할 비밀번호 정보가 담긴 DTO
     * @param email 현재 로그인된 사용자의 이메일
     */
    void changeUserPassword(ChangePasswordDTO pDTO, String email) throws Exception;

    /**
     * [수정됨] 사용자 프로필 정보 조회 (userNo 기반)
     * @param userNo 현재 로그인된 사용자의 고유 번호(PK)
     * @return 프로필 정보가 담긴 DTO (정보가 없으면 빈 DTO 반환)
     */
    UserProfileDTO getUserProfile(Long userNo);

    /**
     * 사용자 프로필 정보 저장 또는 업데이트
     * @param pDTO 저장/업데이트할 프로필 정보
     * @param userNo 현재 로그인된 사용자의 고유 번호
     */
    void saveOrUpdateUserProfile(UpdateProfileDTO pDTO, Long userNo) throws Exception;

    /**
     * 회원 탈퇴 처리를 위한 메서드 명세
     * @param userNo 탈퇴할 사용자의 고유 번호
     * @param password 비밀번호 확인을 위한 현재 비밀번호
     */
    void deleteUser(Long userNo, String password) throws Exception;

}
