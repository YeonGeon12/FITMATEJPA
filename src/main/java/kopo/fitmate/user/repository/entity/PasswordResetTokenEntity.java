package kopo.fitmate.user.repository.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * PASSWORD_RESET_TOKENS 테이블과 매핑되는 JPA Entity 클래스
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED) // 외부에서 기본 생성자로 객체 생성을 막기 위함
@Entity
@Table(name = "PASSWORD_RESET_TOKENS")
public class PasswordResetTokenEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "TOKEN_ID")
    private Long id; // PK (TOKEN_ID)

    @Column(name = "TOKEN", nullable = false, unique = true, length = 255)
    private String token; // 재설정 토큰 문자열

    // UserEntity와의 관계 설정 (N:1)
    // 한 명의 유저(UserEntity)는 여러 개의 재설정 토큰(PasswordResetTokenEntity)을 가질 수 있다.
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "USER_NO", nullable = false)
    private UserEntity user; // FK (USER_NO)

    @Column(name = "EXPIRY_DATE", nullable = false)
    private LocalDateTime expiryDate; // 토큰 만료 일시

    @Builder
    public PasswordResetTokenEntity(String token, UserEntity user, LocalDateTime expiryDate) {
        this.token = token;
        this.user = user;
        this.expiryDate = expiryDate;
    }
}