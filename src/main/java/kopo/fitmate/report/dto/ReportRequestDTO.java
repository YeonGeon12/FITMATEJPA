package kopo.fitmate.report.dto;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * AI 신체 분석 리포트를 요청하기 위해 사용자가 입력한 데이터를 담는 DTO입니다.
 */
@Getter
@Setter
public class ReportRequestDTO implements Serializable {

    private Integer height;         // 키 (cm)

    private Integer weight;         // 체중 (kg)

    private Integer age;            // 나이 (세)

    private String gender;          // 성별 (남성, 여성)

    private String activityLevel;   // 활동량 (좌식, 가벼운 활동 등)

}