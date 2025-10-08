package kopo.fitmate.diet.dto;

import lombok.Getter;
import lombok.Setter;
import java.util.List;

/**
 * AI가 추천한 주간 식단 전체를 담는 최종 '응답' DTO 입니다.
 * 이 객체는 Service 계층에서 생성되어 Controller를 거쳐 View(화면)로 전달
 * (흐름: Service -> Controller -> View)
 */
@Getter
@Setter
public class DietResponseDTO {

    private String totalCalories; // 하루 총 추천 칼로리

    private List<DailyDietDTO> weeklyDiet; // 일주일 식단 리스트

}