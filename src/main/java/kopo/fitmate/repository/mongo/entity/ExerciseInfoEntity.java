package kopo.fitmate.repository.mongo.entity;

import lombok.Builder;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Getter
@Builder
@Document(collection = "EXERCISE_INFO")
public class ExerciseInfoEntity {

    @Id
    private String id; // _id 필드

    @Field(name = "userId")
    private Long userId; // RDBMS의 USERS.USER_NO와 연결

    @Field(name = "title")
    private String title;

    @Field(name = "requestParams")
    private Map<String, Object> requestParams; // 요청 파라미터

    @Field(name = "routineDetails")
    private List<Map<String, Object>> routineDetails; // 상세 루틴

    @Field(name = "createdAt")
    private Date createdAt; // 생성일시
}