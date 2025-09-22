package kopo.fitmate.history.dto;

import kopo.fitmate.repository.mongo.entity.AiReportEntity;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.text.SimpleDateFormat;

@Getter
@Builder
@Slf4j
public class AiReportHistoryDTO {
    private String id;
    private String reportSummary; // AI 리포트 요약 (예: "체중 감량을 위한 맞춤 분석")
    private String createdAt;

    public static AiReportHistoryDTO from(AiReportEntity entity) {
        // 3. DTO 변환 직전에 원본 Entity의 값을 로그로 찍어봅니다.
        if (entity != null) {
            log.info("DTO 변환 시작. Entity ID: " + entity.getId() + ", Summary 값: " + entity.getReportSummary());
        }

        // 날짜 포맷 변경
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd");
        String formattedDate = sdf.format(entity.getCreatedAt());

        return AiReportHistoryDTO.builder()
                .id(entity.getId())
                .reportSummary(entity.getReportSummary())
                .createdAt(formattedDate)
                .build();
    }
}
