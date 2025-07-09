package kopo.fitmate.service.impl;

import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import kopo.fitmate.dto.user.UserFindIdDTO;
import kopo.fitmate.dto.user.UserJoinDTO;
import kopo.fitmate.dto.user.UserLoginDTO;
import kopo.fitmate.repository.UserInfoRepository;
import kopo.fitmate.repository.impl.UserInfoEntity;
import kopo.fitmate.service.IUserInfoService;
import kopo.fitmate.util.CmmUtil;
import kopo.fitmate.util.EncryptUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * 사용자 정보 관련 서비스 구현 클래스
 */
@Service
@RequiredArgsConstructor
public class UserInfoService implements IUserInfoService {

    private final UserInfoRepository userInfoRepository;

    /**
     * 아이디 중복 여부 체크
     *
     * @param userId 사용자 입력 아이디
     * @return true: 중복 / false: 사용 가능
     * @throws Exception 암호화/조회 중 예외 발생 시
     */
    @Override
    public boolean isUserIdDuplicate(String userId) throws Exception {
        return userInfoRepository.findByUserId(CmmUtil.nvl(userId)).isPresent();
    }

    /**
     * 회원가입 처리
     *
     * @param dto 회원가입 DTO
     * @throws Exception 암호화, 저장 중 예외 발생 시
     */
    @Override
    @Transactional
    public void registerUser(UserJoinDTO dto) throws Exception {
        String userId = CmmUtil.nvl(dto.getUserId());
        String userName = CmmUtil.nvl(dto.getUserName());
        String rawPassword = CmmUtil.nvl(dto.getPassword());
        String rawEmail = CmmUtil.nvl(dto.getEmail());

        String hashedPassword = EncryptUtil.encHashSHA256(rawPassword);
        String encryptedEmail = EncryptUtil.encAES128CBC(rawEmail);

        UserInfoEntity user = UserInfoEntity.builder()
                .userId(userId)
                .userName(userName)
                .password(hashedPassword)
                .email(encryptedEmail)
                .regId(userId)
                .regDt(LocalDateTime.now())
                .build();

        userInfoRepository.save(user);
    }

    /**
     * 로그인 처리
     *
     * @param dto 로그인 DTO
     * @return 로그인 성공 여부
     * @throws Exception 암호화, 조회 중 예외 발생 시
     */
    @Override
    public boolean loginUser(UserLoginDTO dto) throws Exception {
        String userId = CmmUtil.nvl(dto.getUserId());
        String inputPassword = EncryptUtil.encHashSHA256(CmmUtil.nvl(dto.getPassword()));

        Optional<UserInfoEntity> userOpt = userInfoRepository.findByUserId(userId);
        return userOpt.map(user -> user.getPassword().equals(inputPassword)).orElse(false);
    }

    /**
     * 사용자 이름과 이메일로 DB에서 사용자 정보를 조회
     *
     * @param dto 이름과 이메일 정보
     * @return 사용자 정보가 있을 경우 Optional로 반환
     * @throws Exception 암호화 처리 중 예외 발생 시
     */
    @Override
    public Optional<UserInfoEntity> findUserIdByNameAndEmail(UserFindIdDTO dto) throws Exception {
        String userName = CmmUtil.nvl(dto.getUserName());
        String email = EncryptUtil.encAES128CBC(CmmUtil.nvl(dto.getEmail())); // 이메일은 암호화되어 저장되므로 암호화 후 비교
        return userInfoRepository.findByUserNameAndEmail(userName, email);
    }
}
