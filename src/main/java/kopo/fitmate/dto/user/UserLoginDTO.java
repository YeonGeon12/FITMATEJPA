// ✅ UserLoginDTO - 로그인 DTO
package kopo.fitmate.dto.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserLoginDTO {

    // 사용자 ID
    @NotBlank(message = "아이디를 입력하세요.")
    @Size(min = 6, max = 20, message = "아이디는 6~20자 이내로 입력해주세요.")
    private String userId;

    // 사용자 비밀번호 (입력)
    @NotBlank(message = "비밀번호를 입력하세요.")
    private String password;
}
