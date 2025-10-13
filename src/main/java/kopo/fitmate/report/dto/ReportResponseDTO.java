package kopo.fitmate.report.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * AI가 생성한 신체 분석 리포트 결과를 담는 최종 응답 DTO입니다.
 */
@Data
@NoArgsConstructor
public class ReportResponseDTO implements Serializable {

    // 1. AI 종합 분석 요약 (2-3줄)
    private String summary;

    // 2. 핵심 지표
    @JsonProperty("BMI") // JSON의 "BMI" 키와 매핑
    private String bmi; // 체질량지수

    @JsonProperty("BMR") // JSON의 "BMR" 키와 매핑
    private String bmr; // 기초대사량

    @JsonProperty("TDEE") // JSON의 "TDEE" 키와 매핑
    private String tdee; // 활동대사량 (권장 섭취 칼로리)

    // 3. AI 조언
    private String exerciseAdvice; // 운동 조언

    private String dietAdvice;     // 식단 조언

}