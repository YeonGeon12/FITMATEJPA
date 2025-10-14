package kopo.fitmate.history.dto;

import lombok.Builder;
import lombok.Data;

/**
 * '내 기록 보기' 화면의 목록에 표시될 개별 항목(운동 또는 식단)의 요약 정보를 담는 DTO이다.
 * Service 계층에서 Entity 데이터를 이 DTO로 변환하여 Controller에 전달합니다.
 */
@Data
@Builder // 빌더 패턴을 사용하여 객체를 안전하고 편리하게 생성할 수 있게 합니다.
public class HistoryItemDTO {

    /**
     * 상세 조회를 위한 각 기록의 고유 ID입니다.
     * MongoDB에 저장된 Document의 ID 값(_id)이 여기에 담깁니다.
     */
    private String id;

    /**
     * 해당 기록이 '운동 루틴'인지 '식단'인지를 구분하는 타입입니다.
     * 이 값을 기준으로 화면에서 아이콘이나 탭을 구분할 수 있습니다.
     * 예: "운동 루틴", "식단", "리포트"
     */
    private String type;

    /**
     * 사용자가 해당 기록을 저장한 날짜와 시간입니다.
     * 목록을 최신순으로 정렬하는 기준이 됩니다.
     * 예: "2025.10.12 23:59:59"
     */
    private String regDt;

    /**
     * 목록에 표시될 간단한 요약 정보입니다.
     * 운동 루틴의 경우 '운동 목표'가, 식단의 경우 '식단 유형'이 이 필드에 담깁니다.
     * 예: "근력 증가", "체중 감량 식단", "리포트"
     */
    private String summary;

}