package kopo.fitmate.dto.user;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

/**
 * 회원 탈퇴 시, 비밀번호 확인을 위해 사용하는 DTO
 */
@Getter
@Setter
public class WithdrawDTO {
    @NotBlank(message = "비밀번호를 입력해주세요.")
    private String password;
}
