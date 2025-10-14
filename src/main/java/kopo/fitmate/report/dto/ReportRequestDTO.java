package kopo.fitmate.report.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * AI 신체 분석 리포트를 요청하기 위해 사용자가 입력한 데이터를 담는 DTO입니다.
 */
@Getter
@Setter
public class ReportRequestDTO implements Serializable {

    // @NotNull: 이 필드는 비어 있을 수 없음을 의미합니다.
    // message: 유효성 검사에 실패했을 때 보여줄 오류 메시지입니다.
    @NotNull(message = "키를 입력해주세요.")
    @Min(value = 100, message = "키는 100cm 이상이어야 합니다.")
    @Max(value = 250, message = "키는 250cm 이하이어야 합니다.")
    private Integer height;         // 키 (cm)

    @NotNull(message = "체중을 입력해주세요.")
    @Min(value = 30, message = "체중은 30kg 이상이어야 합니다.")
    @Max(value = 300, message = "체중은 300kg 이하이어야 합니다.")
    private Integer weight;         // 체중 (kg)

    @NotNull(message = "나이를 입력해주세요.")
    @Min(value = 1, message = "나이는 1세 이상이어야 합니다.")
    @Max(value = 120, message = "나이는 120세 이하이어야 합니다.")
    private Integer age;            // 나이 (세)

    private String gender;          // 성별 (남성, 여성)

    private String activityLevel;   // 활동량 (좌식, 가벼운 활동 등)

}