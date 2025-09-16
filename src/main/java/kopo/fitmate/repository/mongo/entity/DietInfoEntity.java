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
@Document(collection = "DIET_INFO")
public class DietInfoEntity {

    @Id
    private String id;

    @Field(name = "userId")
    private Long userId;

    @Field(name = "dietType")
    private String dietType;

    @Field(name = "calories")
    private Map<String, Object> calories; // 요청 파라미터

    @Field(name = "mealPlan")
    private List<Map<String, Object>> mealPlan; // 상세 식단

    @Field(name = "createdAt")
    private Date createdAt;
}
    