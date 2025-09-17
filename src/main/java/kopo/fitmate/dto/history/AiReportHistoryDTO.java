package kopo.fitmate.dto.history;

import kopo.fitmate.repository.mongo.entity.AiReportEntity;
import lombok.Builder;
import lombok.Getter;

import java.text.SimpleDateFormat;

/**
 * 저장된 AI 리포트 목록을 화면에 전달하기 위한 DTO
 */
@Getter
@Builder
public class AiReportHistoryDTO {

    private String id;
    private String reportSummary; // 리포트 요약 (제목으로 사용)
    private String createdAt;

    public static AiReportHistoryDTO from(AiReportEntity entity) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd");
        return AiReportHistoryDTO.builder()
                .id(entity.getId())
                .reportSummary(entity.getReportSummary()) // 요약을 제목처럼 사용
                .createdAt(sdf.format(entity.getCreatedAt()))
                .build();
    }
}
