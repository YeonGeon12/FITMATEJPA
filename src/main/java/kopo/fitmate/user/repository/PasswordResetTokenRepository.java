package kopo.fitmate.user.repository;

import kopo.fitmate.user.repository.entity.PasswordResetTokenEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * PASSWORD_RESET_TOKENS 테이블에 접근하기 위한 JPARepository 인터페이스
 */
public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetTokenEntity, Long> {

    /**
     * Spring Data Jpa의 쿼리 메소드 규칙에 따라 자동으로 쿼리를 생성
     * 'token' 필드의 값을 기준으로 PASSWORD_RESET_TOKENS 테이블에서 데이터를 조회
     * @param token 조회할 토큰 문자열
     * @return 토큰 정보가 담긴 Entity (없을 경우 Optional.empty())
     */
    Optional<PasswordResetTokenEntity> findByToken(String token);

}
