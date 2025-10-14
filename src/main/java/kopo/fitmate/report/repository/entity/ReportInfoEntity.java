package kopo.fitmate.report.repository.entity;

import kopo.fitmate.report.dto.ReportRequestDTO;
import kopo.fitmate.report.dto.ReportResponseDTO;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "REPORT_INFO") // MongoDB의 "REPORT_INFO" 컬렉션과 매핑
public class ReportInfoEntity {

    @Id
    private String id; // MongoDB Document의 고유 ID

    private String userId;   // 저장한 사용자의 아이디
    private String regDt;    // 저장한 날짜

    // 분석 요청 시 사용자가 입력했던 원본 데이터
    private ReportRequestDTO requestData;

    // AI가 생성한 분석 결과 데이터
    private ReportResponseDTO responseData;
}