package kopo.fitmate.service;

import kopo.fitmate.dto.user.JoinDTO;
import org.springframework.security.core.userdetails.UserDetailsService;

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

}
