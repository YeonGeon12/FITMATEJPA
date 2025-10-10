package kopo.fitmate.diet.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * AI가 추천한 주간 식단 전체를 담는 최종 응답 DTO
 * (Service -> Controller -> View)
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DietResponseDTO implements Serializable {

    // MealDTO 객체들을 담을 주간 식단 리스트
    private List<MealDTO> weeklyDiet;

}