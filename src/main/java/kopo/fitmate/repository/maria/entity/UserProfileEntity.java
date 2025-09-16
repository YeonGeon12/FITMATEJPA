package kopo.fitmate.repository.maria.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal; // BigDecimal 임포트
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "USER_PROFILES")
public class UserProfileEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "PROFILE_ID")
    private Long profileId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USER_NO", nullable = false)
    private UserEntity user;

    // [수정 사항] Double -> BigDecimal 타입으로 변경하여 정확한 소수점을 다루도록 합니다.
    @Column(name = "HEIGHT", precision = 5, scale = 2)
    private BigDecimal height; // 키 (cm)

    // [수정 사할] Double -> BigDecimal 타입으로 변경
    @Column(name = "WEIGHT", precision = 5, scale = 2)
    private BigDecimal weight; // 몸무게 (kg)

    @Column(name = "AGE")
    private Integer age;

    @Column(name = "GENDER", length = 10)
    private String gender;

    @Column(name = "ACTIVITY_LEVEL", length = 20)
    private String activityLevel;

    @UpdateTimestamp
    @Column(name = "UPDATED_AT")
    private LocalDateTime updatedAt;

    // [수정됨] Builder의 파라미터 타입을 BigDecimal로 변경
    @Builder
    public UserProfileEntity(UserEntity user, BigDecimal height, BigDecimal weight, Integer age, String gender, String activityLevel) {
        this.user = user;
        this.height = height;
        this.weight = weight;
        this.age = age;
        this.gender = gender;
        this.activityLevel = activityLevel;
    }

    // [수정됨] updateProfile 메서드의 파라미터 타입을 BigDecimal로 변경
    public void updateProfile(BigDecimal height, BigDecimal weight, Integer age, String gender, String activityLevel) {
        this.height = height;
        this.weight = weight;
        this.age = age;
        this.gender = gender;
        this.activityLevel = activityLevel;
    }
}

