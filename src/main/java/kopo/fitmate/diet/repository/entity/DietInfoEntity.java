package kopo.fitmate.diet.repository.entity;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import java.util.List;

/**
 * MongoDB의 'DIET_INFO' 컬렉션과 매핑되는 메인 Entity 클래스입니다.
 * 사용자의 요청과 AI의 응답을 모두 포함하여 하나의 문서로 저장됩니다.
 */
@Getter
@Setter
@Document(collection = "DIET_INFO")
public class DietInfoEntity {

    @Id
    private String id;

    @Field(name = "user_id")
    private String userId;

    // --- 사용자가 요청한 정보 (DietRequestDTO로부터) ---
    private int height;
    private int weight;
    private String gender;

    @Field(name = "diet_goal")
    private String dietGoal;


    // --- AI가 응답한 추천 결과 (DietResponseDTO로부터) ---
    @Field(name = "total_calories")
    private String totalCalories;

    @Field(name = "weekly_diet")
    private List<DailyDietEmbed> weeklyDiet;


    @Field(name = "reg_dt")
    private String regDt;
}

/**
 * MongoDB 문서 내부에 '하루치' 식단 정보를 저장하기 위한 클래스
 */
@Getter
@Setter
class DailyDietEmbed {

    private String day;
    private List<MealEmbed> meals; // MealEmbed 클래스를 리스트로 포함
}