package kopo.fitmate.history.service;

import kopo.fitmate.history.dto.AiReportHistoryDTO;
import kopo.fitmate.history.dto.DietHistoryDTO;
import kopo.fitmate.history.dto.ExerciseHistoryDTO;
import kopo.fitmate.repository.mongo.entity.AiReportEntity;
import kopo.fitmate.repository.mongo.entity.DietInfoEntity;
import kopo.fitmate.repository.mongo.entity.ExerciseInfoEntity;

import java.util.List;

public interface IHistoryService {

    // 목록 조회
    List<ExerciseHistoryDTO> getExerciseHistory(Long userNo);   // 저장된 운동 기록 가져오기
    List<DietHistoryDTO> getDietHistory(Long userNo);           // 저장된 식단 기록 가져오기
    List<AiReportHistoryDTO> getAiReportHistory(Long userNo);   // 저장된 AI 리포트 가져오기

    // 상세 조회
    ExerciseInfoEntity getExerciseDetail(String id);
    DietInfoEntity getDietDetail(String id);
    AiReportEntity getAiReportDetail(String id);

    // --- [추가] 삭제 ---
    void deleteExerciseHistory(String id);
    void deleteDietHistory(String id);
    void deleteAiReportHistory(String id);

}
