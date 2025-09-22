package kopo.fitmate.history.repository.entity;

import lombok.Builder;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * MongoDB의 EXERCISE_INFO 컬렉션과 매핑되는 Document 클래스
 */
@Getter
@Builder
@Document(collection = "EXERCISE_INFO")
public class ExerciseInfoEntity {

    @Id
    private String id; // MongoDB의 고유 ID (_id)

    @Field(name = "userId")
    private Long userId; // RDBMS의 USERS.USER_NO와 연결되는 외래 키 역할

    @Field(name = "title")
    private String title; // 추천 제목

    @Field(name = "requestParams")
    private Map<String, Object> requestParams; // 사용자가 요청한 파라미터

    @Field(name = "routineDetails")
    private List<Map<String, Object>> routineDetails; // AI가 생성한 상세 루틴

    @Field(name = "createdAt")
    private Date createdAt; // 생성일시
}
