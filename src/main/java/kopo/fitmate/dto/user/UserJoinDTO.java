// ✅ UserJoinDTO - 회원 가입 DTO
package kopo.fitmate.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserJoinDTO {

    // 사용자 ID
    @NotBlank(message = "아이디를 입력하세요.")
    @Size(min = 6, max = 20, message = "아이디는 6~20자 이내로 입력해주세요.")
    private String userId;

    // 사용자 비밀번호 (입력)
    @NotBlank(message = "비밀번호를 입력하세요.")
    @Pattern(
            regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*#?&])[A-Za-z\\d@$!%*#?&]{6,20}$",
            message = "비밀번호는 영문, 숫자, 특수문자를 포함한 6~20자리여야 합니다."
    )
    private String password;

    // 비밀번호 재확인용 필드
    @NotBlank(message = "비밀번호 확인을 입력하세요.")
    private String passwordCheck;

    // 사용자 이름
    @NotBlank(message = "이름을 입력하세요.")
    @Size(min = 2, max = 20, message = "이름은 2~20자 이내로 입력해주세요.")
    private String userName;

    // 사용자 이메일
    @NotBlank(message = "이메일을 입력하세요.")
    @Email(message = "올바른 이메일 형식이 아닙니다.")
    private String email;

    // 이메일 인증번호 (6자리)
    @NotBlank(message = "인증번호를 입력하세요.")
    @Size(min = 6, max = 6, message = "인증번호는 6자리여야 합니다.")
    private String authNumber;
}
