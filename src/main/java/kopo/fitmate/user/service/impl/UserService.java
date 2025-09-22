package kopo.fitmate.user.service.impl;

import jakarta.transaction.Transactional;
import kopo.fitmate.repository.maria.UserProfileRepository;
import kopo.fitmate.repository.maria.UserRepository;
import kopo.fitmate.repository.maria.entity.UserEntity;
import kopo.fitmate.repository.maria.entity.UserProfileEntity;
import kopo.fitmate.repository.mongo.AiReportRepository;
import kopo.fitmate.repository.mongo.DietInfoRepository;
import kopo.fitmate.repository.mongo.ExerciseInfoRepository;
import kopo.fitmate.global.config.util.CmmUtil;
import kopo.fitmate.global.config.util.DateUtil;
import kopo.fitmate.global.config.util.EncryptUtil;
import kopo.fitmate.user.dto.*;
import kopo.fitmate.user.service.IUserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails; // 로그인 메서드
import org.springframework.security.core.userdetails.UsernameNotFoundException; // 예외 처리 메서드
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Service("UserService") // 서비스 이름 명시
public class UserService implements IUserService {

    // JPA Repositories
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserProfileRepository userProfileRepository;


    // MongoDB Repositories
    private final AiReportRepository aiReportRepository;
    private final DietInfoRepository dietInfoRepository;
    private final ExerciseInfoRepository exerciseInfoRepository;

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
     * 사용자 프로필 정보 조회 로직
     */
    @Override
    public Optional<UserProfileDTO> getUserProfile(Long userNo) {
        log.info(this.getClass().getName() + ".getUserProfile Start!");

        // userNo를 기준으로 프로필 정보를 조회
        Optional<UserProfileEntity> profileEntityOptional = userProfileRepository.findByUser_UserNo(userNo);

        // 조회된 엔티티가 존재하면 DTO로 변환하여 반환
        if (profileEntityOptional.isPresent()) {
            UserProfileEntity profileEntity = profileEntityOptional.get();
            UserProfileDTO dto = UserProfileDTO.builder()
                    .height(profileEntity.getHeight())
                    .weight(profileEntity.getWeight())
                    .age(profileEntity.getAge())
                    .gender(profileEntity.getGender())
                    .activityLevel(profileEntity.getActivityLevel())
                    .build();
            return Optional.of(dto);
        }

        // 정보가 없으면 빈 Optional 객체 반환
        return Optional.empty();
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

        // 3. MongoDB에 저장된 모든 관련 데이터 삭제
        exerciseInfoRepository.deleteAllByUserId(userNo);
        dietInfoRepository.deleteAllByUserId(userNo);
        aiReportRepository.deleteAllByUserId(userNo);
        log.info("MongoDB data deleted for userNo: " + userNo);

        // 4. RDBMS에 저장된 사용자 데이터 삭제 (UserProfile은 Cascade 설정으로 자동 삭제)
        userRepository.delete(userEntity);
        log.info("RDBMS data deleted for userNo: " + userNo);

        log.info(this.getClass().getName() + ".deleteUser End!");
    }


}
