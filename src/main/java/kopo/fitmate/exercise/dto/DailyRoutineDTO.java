package kopo.fitmate.exercise.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * 하루치 운동 루틴 정보를 담는 DTO
 * (Service -> Controller -> View)
 */
@NoArgsConstructor          // ✅ 무인자 생성자
@Data
@AllArgsConstructor // 간단한 생성을 위해 모든 필드 생성자 추가
public class DailyRoutineDTO implements Serializable {

    private String day;          // 요일 (예: 월)

    private String bodyPart;     // 운동 부위 (예: 가슴)

    private String exerciseName; // 운동 이름 (예: 벤치프레스)

    private String sets;         // 세트 (예: 4)

    private String reps;         // 횟수 (예: 10-12)

    private String restTime;     // 휴식 시간 (예: 60초)

}
