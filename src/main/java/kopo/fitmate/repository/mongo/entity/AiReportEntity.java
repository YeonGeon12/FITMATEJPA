package kopo.fitmate.repository.mongo.entity;

import lombok.Builder;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Date;
import java.util.Map;

@Getter
@Builder
@Document(collection = "AI_REPORTS")
public class AiReportEntity {
    @Id
    private String id;

    @Field(name = "userId")
    private Long userId;

    @Field(name = "reportSummary")
    private String reportSummary;

    @Field(name = "keyMetrics")
    private Map<String, Object> keyMetrics; // 핵심 지표

    @Field(name = "recommendations")
    private Map<String, Object> recommendations; // 맞춤 조언

    @Field(name = "createdAt")
    private Date createdAt;
}