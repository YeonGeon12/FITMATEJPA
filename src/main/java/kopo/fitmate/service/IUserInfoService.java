package kopo.fitmate.service;

import kopo.fitmate.dto.user.UserJoinDTO;
import kopo.fitmate.dto.user.UserLoginDTO;

/**
 * 사용자 정보 관련 서비스 인터페이스
 */
public interface IUserInfoService {

    /**
     * 아이디 중복 여부 체크
     *
     * @param userId 사용자 입력 아이디
     * @return true: 중복 / false: 사용 가능
     * @throws Exception 예외 발생 시
     */
    boolean isUserIdDuplicate(String userId) throws Exception;

    /**
     * 회원가입 처리
     *
     * @param dto 사용자 회원가입 정보 (ID, PW, 이름, 이메일 등)
     * @throws Exception 암호화 또는 저장 중 예외 발생 가능
     */
    void registerUser(UserJoinDTO dto) throws Exception;

    /**
     * 로그인 처리
     *
     * @param dto 사용자 로그인 정보 (ID, PW)
     * @return 로그인 성공 여부
     * @throws Exception 암호화 또는 조회 중 예외 발생 가능
     */
    boolean loginUser(UserLoginDTO dto) throws Exception;
}
