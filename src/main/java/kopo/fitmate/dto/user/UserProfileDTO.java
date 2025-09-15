package kopo.fitmate.dto.user;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal; // BigDecimal 임포트


/**
 * 조회된 사용자 프로필 정보를 화면으로 전달하기 위한 DTO
 */
@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserProfileDTO {

    private BigDecimal height;
    private BigDecimal weight;
    private Integer age;
    private String gender;
    private String activityLevel;

}
