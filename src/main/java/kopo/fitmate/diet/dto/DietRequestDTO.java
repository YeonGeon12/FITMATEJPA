package kopo.fitmate.diet.dto;


import lombok.Getter;
import lombok.Setter;

/**
 * AI 식단 추천을 요청하기 위해 사용자가 폼에 입력한 데이터를 담는 DTO 입니다.
 * 이 객체는 Controller 계층에서 생성되어 Service 계층으로 전달됩니다.
 * (흐름: View -> Controller -> Service)
 */
@Getter
@Setter
public class DietRequestDTO {

    // 나의 신체 정보
    private int height;         // 키
    private int weight;         // 체중
    private String gender;      // 성별

    // 식단 목표 설정
    private String dietGoal;    // 식단 목표 (예: 체중 감량, 근력 증가)


}
