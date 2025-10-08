package kopo.fitmate.exercise.repository.entity;

import lombok.Getter;
import lombok.Setter;

/**
 * MongoDB 문서 내에 주간 루틴(객체 배열)을 저장하기 위한 클래스
 */
@Getter
@Setter
public class DailyRoutineEmbed {

    private String day;
    private String bodyPart;
    private String exerciseName;
    private String sets;
    private String reps;
    private String restTime;

}