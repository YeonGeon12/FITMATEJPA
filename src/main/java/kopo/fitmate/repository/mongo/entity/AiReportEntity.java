package kopo.fitmate.repository.mongo.entity;

import lombok.Builder;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Date;
import java.util.Map;

/**
 * MongoDB의 AI_REPORTS 컬렉션과 매핑되는 Document 클래스
 */
@Getter
@Builder
@Document(collection = "AI_REPORTS")
public class AiReportEntity {
    @Id
    private String id; // MongoDB의 고유 ID (_id)

    @Field(name = "userId")
    private Long userId; // RDBMS의 USERS.USER_NO와 연결되는 외래 키 역할

    @Field(name = "reportSummary")
    private String reportSummary; // 리포트 요약

    @Field(name = "keyMetrics")
    private Map<String, Object> keyMetrics; // 핵심 지표

    @Field(name = "recommendations")
    private Map<String, Object> recommendations; // 맞춤 조언

    @Field(name = "createdAt")
    private Date createdAt; // 생성일시
}
