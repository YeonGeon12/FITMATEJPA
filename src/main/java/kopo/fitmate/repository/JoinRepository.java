package kopo.fitmate.repository;

import kopo.fitmate.repository.entity.JoinEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface JoinRepository extends JpaRepository<JoinEntity, Long> {

    /**
     * 이메일로 사용자 정보 조회 (중복 가입 방지용)
     */
    Optional<JoinEntity> findByEmail(String email);

}