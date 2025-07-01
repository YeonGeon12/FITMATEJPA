package kopo.fitmate.repository;

import kopo.fitmate.repository.impl.UserInfoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserInfoRepository extends JpaRepository<UserInfoEntity, String> {

    // 사용자 ID로 조회 (로그인, 회원정보 확인 등)
    Optional<UserInfoEntity> findByUserId(String userId);

    // 이메일 중복 확인
    boolean existsByEmail(String email);

    // 이름 + 이메일 조합으로 아이디 찾기
    Optional<UserInfoEntity> findByUserNameAndEmail(String userName, String email);

    // 아이디 + 이메일로 비밀번호 찾기
    Optional<UserInfoEntity> findByUserIdAndEmail(String userId, String email);

}
