package kopo.fitmate.dto.user;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WithdrawDTO {
    @NotBlank(message = "비밀번호를 입력해주세요.")
    private String password;
}
