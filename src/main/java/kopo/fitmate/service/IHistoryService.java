package kopo.fitmate.service;

import kopo.fitmate.dto.history.AiReportHistoryDTO;
import kopo.fitmate.dto.history.DietHistoryDTO;
import kopo.fitmate.dto.history.ExerciseHistoryDTO;

import java.util.List;

public interface IHistoryService {

    // 저장된 운동 기록 가져오기
    List<ExerciseHistoryDTO> getExerciseHistory(Long userNo);

    // 저장된 식단 기록 가져오기
    List<DietHistoryDTO> getDietHistory(Long userNo);

    // 저장된 AI 리포트 가져오기
    List<AiReportHistoryDTO> getAiReportHistory(Long userNo);

}
