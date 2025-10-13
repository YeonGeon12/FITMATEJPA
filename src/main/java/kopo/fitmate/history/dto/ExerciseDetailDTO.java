package kopo.fitmate.history.dto;

import kopo.fitmate.exercise.repository.entity.DailyRoutineEmbed;
import lombok.Builder;
import lombok.Data;
import java.util.List;

/**
 * 저장된 '운동 루틴'의 상세 정보를 담는 DTO입니다.
 */
@Data
@Builder
public class ExerciseDetailDTO {

    private String id;

    private String regDt;

    private String exerciseGoal; // 운동 목표

    private String exerciseLevel; // 운동 수준

    private String workoutLocation; // 운동 장소

    // 주간 운동 루틴 상세 정보
    private List<DailyRoutineEmbed> weeklyRoutine;
}