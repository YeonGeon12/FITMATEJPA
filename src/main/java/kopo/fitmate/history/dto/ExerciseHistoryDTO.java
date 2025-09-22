package kopo.fitmate.history.dto;

import kopo.fitmate.repository.mongo.entity.ExerciseInfoEntity;
import lombok.Builder;
import lombok.Getter;

import java.text.SimpleDateFormat;

/**
 * 저장된 운동 기록 목록을 화면에 전달하기 위한 DTO
 */
@Getter
@Builder
public class ExerciseHistoryDTO {

    private String id; // MongoDB의 Document ID
    private String title; // 루틴 제목
    private String createdAt; // 생성일 (String으로 변환된 형태)

    /**
     * Entity를 DTO로 변환하는 정적 팩토리 메서드
     */
    public static ExerciseHistoryDTO from(ExerciseInfoEntity entity) {
        // 날짜 형식을 "yyyy.MM.dd"로 변환
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd");
        return ExerciseHistoryDTO.builder()
                .id(entity.getId())
                .title(entity.getTitle())
                .createdAt(sdf.format(entity.getCreatedAt()))
                .build();
    }
}
