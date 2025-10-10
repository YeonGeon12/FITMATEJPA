package kopo.fitmate.exercise.dto;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

/**
 * AI 운동 추천을 요청하기 위해 사용자가 입력한 데이터를 담는 DTO
 */
@Getter
@Setter
public class ExerciseRequestDTO implements Serializable {

    // 나의 신체 정보
    private Integer height;             // 키

    private Integer weight;             // 체중

    private String gender;          // 성별

    private Integer age;                // 나이 (더 나은 추천을 위해 추가)

    // 운동 목표 설정
    private String exerciseLevel;   // 운동 수준

    private String exerciseGoal;    // 운동 목표

    private String workoutLocation; // 운동 장소 (더 나은 추천을 위해 추가)

    private List<String> bodyParts; // 희망 운동 부위 (여러 개 선택 가능)

}
