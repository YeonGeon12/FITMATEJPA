package kopo.fitmate.repository;

import kopo.fitmate.repository.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * 사용자 정보에 접근하기 위한 JpaRepository 인터페이스.
 * UserEntity를 관리합니다.
 */
@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {

    // 이메일을 기준으로 사용자 정보를 조회하기 위한 메서드
    Optional<UserEntity> findByEmail(String email);

}
