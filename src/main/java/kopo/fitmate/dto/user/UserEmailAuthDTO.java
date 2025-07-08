// ✅ UserEmailAuthDTO - 이메일 인증번호 확인 DTO
package kopo.fitmate.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserEmailAuthDTO {

    // 사용자 이메일
    @NotBlank(message = "이메일을 입력하세요.")
    @Email(message = "올바른 이메일 형식이 아닙니다.")
    private String email;

    // 이메일 인증번호 (6자리)
    @NotBlank(message = "인증번호를 입력하세요.")
    @Size(min = 6, max = 6, message = "인증번호는 6자리여야 합니다.")
    private String authCode;
}
