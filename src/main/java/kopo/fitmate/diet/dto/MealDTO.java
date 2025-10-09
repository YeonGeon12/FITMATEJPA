package kopo.fitmate.diet.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 하루 식단 중 '한 끼' 식사에 대한 정보를 담는 DTO 입니다.
 * (예: 아침 식사 정보)
 * 이 객체는 DailyDietDTO에 포함되어 사용됩니다.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MealDTO {

        private String time;     // 시간 (예: 아침, 점심, 저녁)

        private String menu;     // 추천 메뉴

        private String calories; // 칼로리

}
