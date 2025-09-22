package kopo.fitmate.exercise.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ExerciseRequestDTO {

    // HTML의 form 태그에서 전달될 데이터들
    // name 속성과 변수명이 일치해야 합니다.
    private String goal;       // 운동 목표 (예: 체중 감량, 근력 증가)
    private String level;      // 운동 레벨 (예: 초급, 중급, 고급)
    private String location;   // 운동 장소 (예: 헬스장, 홈트)
    private String targetArea; // 집중 운동 부위 (예: 등, 가슴, 하체)

}
