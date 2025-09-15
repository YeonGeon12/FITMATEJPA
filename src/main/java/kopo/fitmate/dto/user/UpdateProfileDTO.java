package kopo.fitmate.dto.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * 사용자 프로필 수정을 요청할 때 사용하는 DTO
 */
@Getter
@Setter
public class UpdateProfileDTO {

    @NotNull(message = "키를 입력해주세요.")
    private BigDecimal height;

    @NotNull(message = "몸무게를 입력해주세요.")
    private BigDecimal weight;

    @NotNull(message = "나이를 입력해주세요.")
    private Integer age;

    @NotBlank(message = "성별을 선택해주세요.")
    private String gender;

    @NotBlank(message = "활동 수준을 선택해주세요.")
    private String activityLevel;

}
