package kopo.fitmate.dto.user;

import kopo.fitmate.repository.entity.UserEntity;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

/**
 * Spring Security의 UserDetails 인터페이스를 구현한 DTO.
 * 인증된 사용자 정보를 담는 역할을 합니다.
 */
@Getter
@Setter
public class UserAuthDTO implements UserDetails {

    private Long userNo;
    private String email;
    private String password;
    private String userName;

    // UserEntity를 기반으로 UserAuthDTO를 생성하는 생성자
    public UserAuthDTO(UserEntity userEntity) {
        this.userNo = userEntity.getUserNo();
        this.email = userEntity.getEmail();
        this.password = userEntity.getPassword();
        this.userName = userEntity.getUserName();
    }

    // 사용자의 권한을 반환 (지금은 단순하게 "USER" 역할만 부여)
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singleton(() -> "ROLE_USER");
    }

    // 사용자의 비밀번호(암호화된)를 반환
    @Override
    public String getPassword() {
        return this.password;
    }

    // 사용자의 아이디(이메일)를 반환
    @Override
    public String getUsername() {
        return this.email;
    }

    // 아래 4개는 계정 상태 관련 메서드 (지금은 모두 true로 설정)
    @Override
    public boolean isAccountNonExpired() {
        return true; // 계정이 만료되지 않았음
    }

    @Override
    public boolean isAccountNonLocked() {
        return true; // 계정이 잠기지 않았음
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true; // 자격 증명(비밀번호)이 만료되지 않았음
    }

    @Override
    public boolean isEnabled() {
        return true; // 계정이 활성화되어 있음
    }
}
