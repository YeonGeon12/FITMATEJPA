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
 * MongoDB의 DIET_INFO 컬렉션과 매핑되는 Document 클래스
 */
@Getter
@Builder
@Document(collection = "DIET_INFO")
public class DietInfoEntity {

    @Id
    private String id; // MongoDB의 고유 ID (_id)

    @Field(name = "userId")
    private Long userId; // RDBMS의 USERS.USER_NO와 연결되는 외래 키 역할

    @Field(name = "dietType")
    private String dietType; // 식단 제목

    @Field(name = "calories")
    private Map<String, Object> calories; // 요청 파라미터

    @Field(name = "mealPlan")
    private List<Map<String, Object>> mealPlan; // AI가 생성한 상세 식단

    @Field(name = "createdAt")
    private Date createdAt; // 생성일시
}
