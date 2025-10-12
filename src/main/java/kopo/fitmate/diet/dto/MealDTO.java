package kopo.fitmate.diet.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 하루의 한 끼 식단 정보를 담는 DTO
 * (예: 아침 식사 정보)
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MealDTO implements Serializable {

    private String day;         // 요일 (예: 월)

    private String mealTime;    // 식사 시간 (예: 아침, 점심, 저녁)

    private String menu;        // 메뉴 이름 (예: 닭가슴살 샐러드)

    private String calories;    // 칼로리 (예: 350kcal)

}
