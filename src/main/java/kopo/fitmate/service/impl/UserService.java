package kopo.fitmate.service.impl;

import jakarta.transaction.Transactional;
import kopo.fitmate.dto.user.ChangePasswordDTO;
import kopo.fitmate.dto.user.JoinDTO;
import kopo.fitmate.dto.user.UserAuthDTO;
import kopo.fitmate.repository.UserRepository;
import kopo.fitmate.repository.entity.UserEntity;
import kopo.fitmate.service.IUserService;
import kopo.fitmate.util.CmmUtil;
import kopo.fitmate.util.DateUtil;
import kopo.fitmate.util.EncryptUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails; // 로그인 메서드
import org.springframework.security.core.userdetails.UsernameNotFoundException; // 예외 처리 메서드
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service("UserService") // 서비스 이름 명시
public class UserService implements IUserService {

    // Repository 의존성 주입 (final 키워드로 생성자 주입)
    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    // #################################################### 회원 가입 메서드 ##############################

    /**
     * 회원가입 로직을 수행하는 메서드
     */
    @Override
    @Transactional
    public void insertUserInfo(JoinDTO pDTO) throws Exception {

        log.info("회원가입 서비스 시작");

        // DTO에서 받은 값을 null-safe하게 처리
        String email = CmmUtil.nvl(pDTO.getEmail());
        String password = CmmUtil.nvl(pDTO.getPassword());
        String passwordCheck = CmmUtil.nvl(pDTO.getPasswordCheck());
        String userName = CmmUtil.nvl(pDTO.getUserName());

        // 비즈니스 로직 1: 비밀번호와 비밀번호 확인이 일치하는지 검사
        if (!password.equals(passwordCheck)) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        // 비즈니스 로직 2: 이미 가입된 이메일인지 중복 검사
        if (checkEmailDuplicate(email)) {
            throw new IllegalArgumentException("이미 가입된 이메일입니다.");
        }

        // 비밀번호를 SHA-256으로 암호화
        String encryptedPassword = EncryptUtil.encHashSHA256(password);

        // 현재 날짜와 시간 생성
        String createAt = DateUtil.getDateTime("yyyy-MM-dd HH:mm:ss");

        // DB에 저장하기 위해 DTO를 Entity로 변환
        UserEntity userEntity = UserEntity.builder()
                .email(email)
                .password(encryptedPassword)
                .userName(userName)
                .createAt(createAt)
                .build();

        // JpaRepository를 통해 데이터베이스에 저장
        userRepository.save(userEntity);

        log.info("회원가입 서비스 종료");
    }

    /**
     * 이메일 중복 여부를 확인하는 메서드
     */
    @Override
    public boolean checkEmailDuplicate(String email) {
        log.info("이메일 중복 체크 서비스 시작. 이메일: " + email);
        // findByEmail 메서드로 조회 후, 결과가 존재하는지 여부(true/false)를 반환
        return userRepository.findByEmail(CmmUtil.nvl(email)).isPresent();
    }


    //############################# 여기는 로그인 메서드 #############################################


    /**
     * [로그인 처리의 핵심]
     * Spring Security가 로그인 요청 시 자동으로 호출하는 메서드.
     * @param email 로그인 폼에서 입력된 이메일 주소
     * @return UserDetails 인터페이스를 구현한 객체 (UserAuthDTO)
     * @throws UsernameNotFoundException 해당 이메일의 사용자를 찾지 못했을 때 발생
     */
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        log.info(this.getClass().getName() + ".loadUserByUsername Start!");

        // 1. DB에서 이메일로 사용자 정보 조회
        UserEntity userEntity = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException(email + " not found."));

        // 2. 조회된 사용자 정보를 UserAuthDTO에 담아 반환
        //    이 DTO를 받은 Spring Security가 비밀번호 비교 등을 알아서 처리해 줌
        return new UserAuthDTO(userEntity);
    }


    // ######################### 비밀번호 비밀번호 변경 매서드 ##########################################

    /**
     * [새로 추가] 비밀번호 변경 로직을 수행하는 메서드
     */
    @Override
    @Transactional
    public void changeUserPassword(ChangePasswordDTO pDTO, String email) throws Exception {
        log.info(this.getClass().getName() + ".changeUserPassword Start!");

        // 1. 이메일을 기준으로 DB에서 사용자 정보를 조회합니다.
        UserEntity userEntity = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException(email + " not found."));

        // 2. 입력받은 '현재 비밀번호'가 DB에 저장된 비밀번호와 일치하는지 확인합니다.
        //    passwordEncoder.matches()가 암호화된 비밀번호를 안전하게 비교해줍니다.
        if (!passwordEncoder.matches(pDTO.getCurrentPassword(), userEntity.getPassword())) {
            throw new IllegalArgumentException("현재 비밀번호가 일치하지 않습니다.");
        }

        // 3. '새 비밀번호'와 '새 비밀번호 확인' 값이 일치하는지 확인합니다.
        if (!pDTO.getNewPassword().equals(pDTO.getNewPasswordCheck())) {
            throw new IllegalArgumentException("새 비밀번호가 일치하지 않습니다.");
        }

        // 4. 모든 검증을 통과하면, 새 비밀번호를 암호화하여 DB에 업데이트합니다.
        userEntity.setPassword(passwordEncoder.encode(pDTO.getNewPassword()));

        // @Transactional 어노테이션 덕분에 메서드가 종료될 때 변경된 내용이 자동으로 DB에 저장(update)됩니다.
        log.info(this.getClass().getName() + ".changeUserPassword End!");
    }

}
