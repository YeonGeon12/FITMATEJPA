package kopo.fitmate.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserDTO {

    // USERS 테이블의 Column ID와 매칭
    private Long userNo;

    @NotBlank(message = "이메일은 필수 입력 값입니다.")
    @Email(message = "이메일 형식이 올바르지 않습니다.")
    private String email;

    @NotBlank(message = "비밀번호는 필수 입력 값입니다.")
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{6,20}$",
            message = "비밀번호는 영문, 숫자를 포함하여 6~20자 사이여야 합니다.")
    private String password;

    private String passwordCheck; // 비밀번호 확인용

    @NotBlank(message = "이름은 필수 입력 값입니다.")
    private String userName;

    private String createAt;
}