package kopo.fitmate.service.impl;

import jakarta.transaction.Transactional;
import kopo.fitmate.dto.JoinDTO;
import kopo.fitmate.repository.JoinRepository;
import kopo.fitmate.repository.entity.JoinEntity;
import kopo.fitmate.service.IJoinService;
import kopo.fitmate.util.CmmUtil;
import kopo.fitmate.util.DateUtil;
import kopo.fitmate.util.EncryptUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class JoinService implements IJoinService {

    private final JoinRepository userRepository;

    @Override
    @Transactional
    public void insertUserInfo(JoinDTO pDTO) throws Exception {

        log.info(this.getClass().getName() + ".insertUserInfo Start!");

        // 1. CmmUtil.nvl을 이용한 입력 값 null 처리
        String email = CmmUtil.nvl(pDTO.getEmail());
        String password = CmmUtil.nvl(pDTO.getPassword());
        String passwordCheck = CmmUtil.nvl(pDTO.getPasswordCheck());
        String userName = CmmUtil.nvl(pDTO.getUserName());

        // 2. 비밀번호 일치 여부 확인
        if (!password.equals(passwordCheck)) {
            throw new RuntimeException("비밀번호가 일치하지 않습니다.");
        }

        // 3. 이메일 중복 확인
        userRepository.findByEmail(email).ifPresent(user -> {
            throw new RuntimeException("이미 가입된 이메일입니다.");
        });

        // 4. EncryptUtil을 이용한 비밀번호 암호화 (SHA-256)
        String encryptedPassword = EncryptUtil.encHashSHA256(password);
        log.info("Encrypted Password: " + encryptedPassword);

        // 5. DateUtil을 이용한 가입일자 설정
        String createAt = DateUtil.getDateTime("yyyy-MM-dd HH:mm:ss");

        // DTO를 Entity로 변환하여 저장
        JoinEntity joinEntity = JoinEntity.builder()
                .email(email)
                .password(encryptedPassword) // 암호화된 비밀번호 저장
                .userName(userName)
                .createAt(createAt) // 현재 시간 저장
                .build();

        userRepository.save(joinEntity);

        log.info(this.getClass().getName() + ".insertUserInfo End!");
    }
}
