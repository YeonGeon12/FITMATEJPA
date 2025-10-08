package kopo.fitmate.exercise.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

/**
 * AI가 추천한 주간 운동 루틴 전체를 담는 최종 응답 DTO
 * (Service -> Controller -> View)
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExerciseResponseDTO {

    private List<DailyRoutineDTO> weeklyRoutine; // 주간 루틴 리스트

}
