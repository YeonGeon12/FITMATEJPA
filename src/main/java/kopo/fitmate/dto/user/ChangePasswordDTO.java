package kopo.fitmate.dto.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

/**
 * 비밀번호 변경 폼의 데이터를 Controller로 전달하기 위한 DTO
 */
@Getter
@Setter
public class ChangePasswordDTO {

    @NotBlank(message = "현재 비밀번호를 입력해주세요.")
    private String currentPassword; // 현재 비밀번호

    @NotBlank(message = "새 비밀번호는 필수 입력 값입니다.")
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{6,20}$",
            message = "비밀번호는 영문, 숫자를 포함하여 6~20자 사이여야 합니다.")
    private String newPassword; // 새 비밀번호

    @NotBlank(message = "새 비밀번호 확인을 입력해주세요.")
    private String newPasswordCheck; // 새 비밀번호 확인
}
