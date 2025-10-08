package kopo.fitmate.exercise.repository.entity;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import java.util.List;

/**
 * MongoDB의 'EXERCISE_INFO' 컬렉션과 매핑되는 Entity 클래스
 */
@Getter
@Setter
@Document(collection = "EXERCISE_INFO")
public class ExerciseInfoEntity {

    @Id
    private String id; // MongoDB의 고유 ID

    @Field(name = "user_id")
    private String userId; // 추천을 요청한 사용자 ID (이메일)

    // --- 사용자가 요청한 정보 ---
    private int height; // 키(CM)
    private int weight; // 체중(KG)
    private String gender; // 성별(MALE/FEMALE)
    private int age; // 나이(AGE)

    @Field(name = "exercise_level")
    private String exerciseLevel; // 운동 강도(초보, 중수, 고수)

    @Field(name = "exercise_goal")
    private String exerciseGoal; // 운동 목표

    @Field(name = "workout_location")
    private String workoutLocation; // 운동 시간

    @Field(name = "body_parts")
    private List<String> bodyParts; // 운동 루틴

    // --- AI가 응답한 추천 결과 ---
    @Field(name = "weekly_routine")
    private List<DailyRoutineEmbed> weeklyRoutine; // 주간 루틴

    @Field(name = "reg_dt")
    private String regDt; // 추천받은 날짜
}
