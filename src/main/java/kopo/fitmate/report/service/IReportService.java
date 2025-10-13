package kopo.fitmate.report.service;

import kopo.fitmate.report.dto.ReportRequestDTO;
import kopo.fitmate.report.dto.ReportResponseDTO;

/**
 * AI 신체 분석 리포트 기능의 비즈니스 로직을 정의하는 인터페이스입니다.
 */
public interface IReportService {

    /**
     * 사용자의 신체 정보를 받아 AI에게 분석을 요청하고, 그 결과를 반환하는 메서드
     *
     * @param requestDTO 사용자가 폼에 입력한 신체 정보
     * @return AI가 생성한 신체 분석 리포트
     */
    ReportResponseDTO getReport(ReportRequestDTO requestDTO) throws Exception;

}