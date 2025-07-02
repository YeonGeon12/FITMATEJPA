package kopo.fitmate.repository.impl;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "USER_INFO")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserInfoEntity {

    @Id
    @Column(name = "USER_ID", nullable = false, length = 50)
    private String userId;

    @Column(name = "USER_NAME", nullable = false, length = 500)
    private String userName;

    @Column(name = "PASSWORD", nullable = false, length = 1)
    private String password;

    @Column(name = "EMAIL", nullable = false, length = 100)
    private String email;

    @Column(name = "REG_ID", length = 4)
    private String regId;

    @Column(name = "REG_DT")
    private LocalDateTime regDt;

    @Column(name = "CHG_ID", length = 4)
    private String chgId;

    @Column(name = "CHG_DT")
    private LocalDateTime chgDt;

}
