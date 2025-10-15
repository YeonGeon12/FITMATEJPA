package kopo.fitmate.dictionary.dto;

import lombok.Builder;
import lombok.Data;

/**
 * 원본(영어) 운동 정보와 번역된(한국어) 정보를 함께 담는 DTO
 */
@Data
@Builder
public class TranslatedExerciseDTO {

    // 원본 ExerciseDTO 정보
    private ExerciseDTO original;

    // 번역된 정보
    private String translatedName;
    private String translatedInstructions;

}