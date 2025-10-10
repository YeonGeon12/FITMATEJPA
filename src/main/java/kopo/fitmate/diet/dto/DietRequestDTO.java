package kopo.fitmate.diet.dto;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * AI 식단 추천을 요청하기 위해 사용자가 선택한 데이터를 담는 DTO
 * (View -> Controller)
 */
@Getter
@Setter
public class DietRequestDTO implements Serializable {

    // 사용자가 선택할 식단 유형 (예: 체중 감량, 근력 증가 등)
    private String dietType;

}