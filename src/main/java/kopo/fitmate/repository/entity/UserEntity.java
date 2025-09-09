package kopo.fitmate.repository.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

/**
 * 사용자 정보를 나타내는 엔티티 클래스.
 * DB의 USERS 테이블과 매핑됩니다.
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "USERS")
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "USER_NO")
    private Long userNo; // 사용자 고유 번호 (PK)

    @Column(name = "EMAIL", unique = true, nullable = false)
    private String email; // 사용자 이메일 (로그인 ID로 사용)

    @Column(name = "PASSWORD", nullable = false)
    private String password; // 암호화된 비밀번호

    @Column(name = "USER_NAME", nullable = false)
    private String userName; // 사용자 이름

    @Column(name = "CREATE_AT", nullable = false)
    private String createAt; // 가입일시

    @Builder
    public UserEntity(Long userNo, String email, String password, String userName, String createAt) {
        this.userNo = userNo;
        this.email = email;
        this.password = password;
        this.userName = userName;
        this.createAt = createAt;
    }
}

