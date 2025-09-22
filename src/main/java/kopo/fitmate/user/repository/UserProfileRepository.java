package kopo.fitmate.user.repository;

import kopo.fitmate.user.repository.entity.UserProfileEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserProfileRepository extends JpaRepository<UserProfileEntity, Long> {

    /**
     * Spring Data JPA의 쿼리 메소드 규칙에 따라 메소드를 추가합니다.
     * UserProfileEntity의 'user' 필드(UserEntity)에 있는 'userNo' 필드를 기준으로 조회하는 쿼리를 자동으로 생성합니다.
     */
    Optional<UserProfileEntity> findByUser_UserNo(Long userNo);

}