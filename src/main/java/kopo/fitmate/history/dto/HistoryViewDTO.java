package kopo.fitmate.history.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * '내 기록 보기(historyView.html)' 페이지 전체에 필요한 데이터 묶음을 담는 DTO입니다.
 * 이 DTO 하나만 Model에 담아 View로 전달하면, 화면에 필요한 모든 데이터를 사용할 수 있습니다.
 */
@Data
@Builder
public class HistoryViewDTO {

    /**
     * 저장된 모든 '운동 루틴' 기록의 요약 정보 리스트입니다.
     * HistoryItemDTO 객체들이 이 리스트에 담깁니다.
     */
    private List<HistoryItemDTO> exerciseList;

    /**
     * 저장된 모든 '식단' 기록의 요약 정보 리스트입니다.
     * HistoryItemDTO 객체들이 이 리스트에 담깁니다.
     */
    private List<HistoryItemDTO> dietList;

}