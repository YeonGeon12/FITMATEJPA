// ✅ UserFindPwDTO - 비밀번호 찾기용 DTO
package kopo.fitmate.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserFindPwDTO {

    // 사용자 ID
    @NotBlank(message = "아이디를 입력하세요.")
    @Size(min = 6, max = 20, message = "아이디는 6~20자 이내여야 합니다.")
    private String userId;

    // 사용자 이메일
    @NotBlank(message = "이메일을 입력하세요.")
    @Email(message = "올바른 이메일 형식이 아닙니다.")
    private String email;
}
