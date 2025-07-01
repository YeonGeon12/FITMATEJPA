// ✅ UserFindIdDTO - 이름과 이메일을 이용해 ID 찾기 DTO
package kopo.fitmate.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserFindIdDTO {

    // 사용자 이름
    @NotBlank(message = "이름을 입력하세요.")
    @Size(min = 2, max = 20, message = "이름은 2~20자 이내로 입력해주세요.")
    private String userName;

    // 사용자 이메일
    @NotBlank(message = "이메일을 입력하세요.")
    @Email(message = "올바른 이메일 형식이 아닙니다.")
    private String email;
}
