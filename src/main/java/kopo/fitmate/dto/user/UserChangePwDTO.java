// ✅ UserChangePwDTO - 비밀번호 변경용 DTO
package kopo.fitmate.dto.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserChangePwDTO {

    // 사용자 ID
    @NotBlank
    private String userId;

    // 새 비밀번호 입력 필드
    @NotBlank(message = "새 비밀번호를 입력하세요.")
    @Pattern(
            regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*#?&])[A-Za-z\\d@$!%*#?&]{6,20}$",
            message = "비밀번호는 영문, 숫자, 특수문자를 포함한 6~20자리여야 합니다."
    )
    private String newPassword;

    // 새 비밀번호 확인용 필드
    @NotBlank(message = "새 비밀번호 확인을 입력하세요.")
    private String newPasswordCheck;
}
