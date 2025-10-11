package kopo.fitmate.diet.repository.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 한 끼 식사 정보를 담는 클래스 DietInfoEntity 내부에 리스트 형태로 저장
 */
@Data
@NoArgsConstructor
public class MealEmbed {

    private String day; // 요일(예: 월, 화, 수)

    private String mealTime; // 끼니 시간(아침, 점심, 저녁)

    private String menu; // 음식 메뉴(예: 치킨, 샐러드)

    private String calories; // 칼로리(예: 300kcal)

}