package kopo.fitmate.diet.repository.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

/**
 * 사용자가 저장한 식단 정보 전체를 담는 메인 Entity
 * MongoDB 컬렉션에 하나의 Document로 저장된다
 */
@Data
@Document(collection = "DIET_INFO") // MongoDB의 "DIET_INFO" 컬렉션과 매핑
public class DietInfoEntity {

    @Id
    private String id; // MongoDB Document의 고유 ID

    private String userId;   // 저장한 사용자의 아이디
    private String regDt;    // 저장한 날짜
    private String dietType; // 추천받은 식단 유형

    // 한 주간의 식단 리스트 (MealEmbed 객체들을 포함)
    private List<MealEmbed> weeklyDiet;

}