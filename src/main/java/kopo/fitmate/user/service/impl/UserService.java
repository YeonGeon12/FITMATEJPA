package kopo.fitmate.user.service.impl;

import jakarta.mail.internet.MimeMessage;
import jakarta.transaction.Transactional;
import kopo.fitmate.global.config.util.CmmUtil;
import kopo.fitmate.global.config.util.DateUtil;
import kopo.fitmate.global.config.util.EncryptUtil;
import kopo.fitmate.user.dto.*;
import kopo.fitmate.user.repository.PasswordResetTokenRepository;
import kopo.fitmate.user.repository.UserProfileRepository;
import kopo.fitmate.user.repository.UserRepository;
import kopo.fitmate.user.repository.entity.PasswordResetTokenEntity;
import kopo.fitmate.user.repository.entity.UserEntity;
import kopo.fitmate.user.repository.entity.UserProfileEntity;
import kopo.fitmate.user.service.IUserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Service("UserService") // 서비스 이름 명시
public class UserService implements IUserService {

    // JPA Repositories
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserProfileRepository userProfileRepository;
    private final PasswordResetTokenRepository tokenRepository;

    // Email Sender
    private final JavaMailSender mailSender;

    // 2. application.yaml에 있는 spring.mail.username 값을 주입받을 변수를 선언합니다.
    @Value("${spring.mail.username}")
    private String fromEmail;


    // ################################# 회원 가입 메서드 ##############################

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


    //############################# 여기는 로그인 메서드 #################################


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

    // ######################## 비밀번호 찾기 매서드 ###############################

    /**
     * 비밀번호 재설정 토큰 생성 로직
     */
    @Override
    @Transactional
    public String createPasswordResetTokenForUser(String email) throws Exception {
        UserEntity userEntity = userRepository.findByEmail(email)
                .orElseThrow(() -> new Exception("해당 이메일로 가입된 사용자를 찾을 수 없습니다."));

        String token = UUID.randomUUID().toString(); // 고유한 랜덤 토큰 생성
        LocalDateTime expiryDate = LocalDateTime.now().plusMinutes(15); // 토큰 만료 시간: 15분

        PasswordResetTokenEntity myToken = PasswordResetTokenEntity.builder()
                .user(userEntity)
                .token(token)
                .expiryDate(expiryDate)
                .build();

        tokenRepository.save(myToken); // 생성된 토큰을 DB에 저장
        return token;
    }

    /**
     * 비밀번호 재설정 이메일 발송 로직
     */
    @Override
    public void sendPasswordResetEmail(String email, String token) throws Exception {
        // 사용자가 클릭할 재설정 URL 생성
        String resetUrl = "http://localhost:11000/user/resetPassword?token=" + token;

        // SimpleMailMessage 대신 MimeMessage를 생성
        MimeMessage mimeMessage = mailSender.createMimeMessage();

        try {
            // MimeMessageHelper를 사용하여 메일 내용을 구성합니다.
            //    'true' 파라미터는 이 메일이 HTML 형식임을 나타냅니다.
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "utf-8");

            helper.setFrom(fromEmail);
            helper.setTo(email);
            helper.setSubject("[FITMATE] 비밀번호 재설정 요청");

            // 5. HTML 태그를 사용하여 이메일 본문을 작성합니다.
            //    <a> 태그를 사용해 클릭 가능한 링크를 만듭니다.
            String htmlContent = "<h2>안녕하세요, FITMATE입니다.</h2>"
                    + "<p>비밀번호를 재설정하려면 아래 링크를 클릭하세요. (링크는 15분간 유효합니다.)</p>"
                    + "<a href='" + resetUrl + "' style='font-size: 16px; color: blue;'>비밀번호 재설정하기</a>";

            helper.setText(htmlContent, true); // setText의 두 번째 인자를 true로 설정해야 HTML로 인식됩니다.

            // 6. 수정된 MimeMessage를 발송합니다.
            mailSender.send(mimeMessage);
            log.info("비밀번호 재설정 이메일(HTML) 발송 완료. 수신자: {}", email);

        } catch (Exception e) {
            log.error("HTML 이메일 발송 중 오류 발생", e);
            throw new Exception("메일 발송에 실패했습니다.");
        }
    }

    /**
     * 토큰 유효성 검증 로직
     */
    @Override
    public boolean validatePasswordResetToken(String token) {
        return tokenRepository.findByToken(token)
                // 토큰이 존재하고(map), 만료 시간이 현재 시간보다 이전이 아닌지(!isBefore) 확인
                .map(t -> !t.getExpiryDate().isBefore(LocalDateTime.now()))
                // 토큰이 존재하지 않으면 false 반환
                .orElse(false);
    }

    /**
     * 비밀번호 재설정 처리 로직
     */
    @Override
    @Transactional
    public void resetPassword(String token, String newPassword) throws Exception {
        // 1. 토큰으로 토큰 엔티티를 찾음
        PasswordResetTokenEntity resetToken = tokenRepository.findByToken(token)
                .orElseThrow(() -> new Exception("유효하지 않은 토큰입니다."));

        // 2. 토큰이 만료되었는지 다시 한번 확인
        if (resetToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            tokenRepository.delete(resetToken); // 만료된 토큰은 DB에서 삭제
            throw new Exception("만료된 토큰입니다. 다시 요청해주세요.");
        }

        // 3. 토큰과 연결된 사용자 정보를 가져옴
        UserEntity user = resetToken.getUser();

        // 4. 새 비밀번호를 암호화하여 사용자 엔티티에 설정
        user.setPassword(passwordEncoder.encode(newPassword));

        // 5. 변경된 사용자 정보를 DB에 저장 (JPA의 Dirty Checking에 의해 자동 update)

        // 6. 사용이 끝난 토큰은 DB에서 즉시 삭제
        tokenRepository.delete(resetToken);

        log.info("사용자 {}의 비밀번호가 성공적으로 재설정되었습니다.", user.getEmail());
    }

    // ######################### 비밀번호 비밀번호 변경 매서드 #############################

    /**
     * 비밀번호 변경 로직을 수행하는 메서드
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

    // ############################### 사용자 프로필 생성 및 조회 로직 ################################################


    /**
     *  기존에 저장된 정보 불러오기
     * @param userNo 유저번호
     * @return 리턴
     */
    @Override
    public Optional<UserProfileDTO> getUserProfile(Long userNo) {
        log.info(this.getClass().getName() + ".getUserProfile Start!");

        Optional<UserProfileEntity> profileEntityOptional = userProfileRepository.findByUser_UserNo(userNo);

        // Entity가 존재하면 DTO로 변환하여 반환하고, 없으면 빈 Optional을 반환
        return profileEntityOptional.map(profile ->
                UserProfileDTO.builder()
                        .height(profile.getHeight())
                        .weight(profile.getWeight())
                        .age(profile.getAge())
                        .gender(profile.getGender())
                        .activityLevel(profile.getActivityLevel())
                        .build()
        );
    }

    /**
     * 사용자 프로필 정보 저장 또는 수정 로직
     */
    @Override
    @Transactional
    public void saveOrUpdateUserProfile(UpdateProfileDTO pDTO, Long userNo) throws Exception {
        log.info(this.getClass().getName() + ".saveOrUpdateUserProfile Start!");

        // 1. userNo로 UserEntity를 찾습니다. (프로필의 주인)
        UserEntity user = userRepository.findById(userNo)
                .orElseThrow(() -> new Exception("User not found!"));

        // 2. 해당 유저의 프로필 정보가 이미 존재하는지 확인합니다.
        Optional<UserProfileEntity> profileOptional = userProfileRepository.findByUser_UserNo(userNo);

        if (profileOptional.isPresent()) {
            // 3-1. 정보가 존재하면, 기존 엔티티를 가져와 정보를 수정합니다. (Update)
            UserProfileEntity existingProfile = profileOptional.get();
            existingProfile.updateProfile(
                    pDTO.getHeight(),
                    pDTO.getWeight(),
                    pDTO.getAge(),
                    pDTO.getGender(),
                    pDTO.getActivityLevel()
            );
        } else {
            // 3-2. 정보가 없으면, 새로운 엔티티를 생성합니다. (Insert)
            UserProfileEntity newProfile = UserProfileEntity.builder()
                    .user(user)
                    .height(pDTO.getHeight())
                    .weight(pDTO.getWeight())
                    .age(pDTO.getAge())
                    .gender(pDTO.getGender())
                    .activityLevel(pDTO.getActivityLevel())
                    .build();
            userProfileRepository.save(newProfile);
        }

        log.info(this.getClass().getName() + ".saveOrUpdateUserProfile End!");
    }

    /**
     * 회원 탈퇴시 데이터 삭제 로직
     * @param userNo 탈퇴할 사용자의 고유 번호
     * @param password 비밀번호 확인을 위한 현재 비밀번호
     * @throws Exception 예외 처리
     */
    @Override
    @Transactional
    public void deleteUser(Long userNo, String password) throws Exception {
        log.info(this.getClass().getName() + ".deleteUser Start!");

        // 1. RDBMS에서 사용자 정보 조회
        UserEntity userEntity = userRepository.findById(userNo)
                .orElseThrow(() -> new Exception("사용자 정보를 찾을 수 없습니다."));

        // 2. 입력된 비밀번호가 맞는지 확인
        if (!passwordEncoder.matches(password, userEntity.getPassword())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        // 4. RDBMS에 저장된 사용자 데이터 삭제 (UserProfile은 Cascade 설정으로 자동 삭제)
        userRepository.delete(userEntity);
        log.info("RDBMS data deleted for userNo: " + userNo);

        log.info(this.getClass().getName() + ".deleteUser End!");
    }


}
