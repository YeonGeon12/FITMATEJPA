package kopo.fitmate.diet.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * '하루치' 식단 전체 정보를 담는 DTO 입니다.
 * 요일 정보와, 해당 요일에 해당하는 여러 끼니(MealDTO)의 리스트를 가집니다.
 * 이 객체는 최종 응답 객체인 DietResponseDTO에 포함됩니다.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DailyDietDTO {

    private String day; // 요일 (예: 월요일)

    private List<MealDTO> meals; // 해당 요일의 끼니 목록

}
