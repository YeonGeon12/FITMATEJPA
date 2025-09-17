package kopo.fitmate.dto.history;

import kopo.fitmate.repository.mongo.entity.DietInfoEntity;
import lombok.Builder;
import lombok.Getter;

import java.text.SimpleDateFormat;

/**
 * 저장된 식단 기록 목록을 화면에 전달하기 위한 DTO
 */
@Getter
@Builder
public class DietHistoryDTO {

    private String id;
    private String dietType; // 식단 제목
    private String createdAt;

    public static DietHistoryDTO from(DietInfoEntity entity) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd");
        return DietHistoryDTO.builder()
                .id(entity.getId())
                .dietType(entity.getDietType())
                .createdAt(sdf.format(entity.getCreatedAt()))
                .build();
    }
}
