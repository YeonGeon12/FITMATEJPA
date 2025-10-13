package kopo.fitmate.history.dto;

import kopo.fitmate.diet.repository.entity.MealEmbed;
import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * 저장된 '식단'의 상세 정보를 담는 DTO입니다.
 */
@Data
@Builder
public class DietDetailDTO {

    private String id;

    private String regDt;

    private String dietType; // 식단 유형

    // 주간 식단 상세 정보
    private List<MealEmbed> weeklyDiet;
}
