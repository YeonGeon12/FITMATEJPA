package kopo.fitmate.service.impl;

import kopo.fitmate.dto.history.AiReportHistoryDTO;
import kopo.fitmate.dto.history.DietHistoryDTO;
import kopo.fitmate.dto.history.ExerciseHistoryDTO;
import kopo.fitmate.repository.mongo.AiReportRepository;
import kopo.fitmate.repository.mongo.DietInfoRepository;
import kopo.fitmate.repository.mongo.ExerciseInfoRepository;
import kopo.fitmate.service.IHistoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service("HistoryService")
public class HistoryService implements IHistoryService {

    // MongoDB Repositories
    private final ExerciseInfoRepository exerciseInfoRepository;
    private final DietInfoRepository dietInfoRepository;
    private final AiReportRepository aiReportRepository;

    @Override
    public List<ExerciseHistoryDTO> getExerciseHistory(Long userNo) {
        log.info(this.getClass().getName() + ".getExerciseHistory Start!");
        // Entity List를 DTO List로 변환하여 반환
        return exerciseInfoRepository.findAllByUserIdOrderByCreatedAtDesc(userNo)
                .stream()
                .map(ExerciseHistoryDTO::from)
                .collect(Collectors.toList());
    }

    @Override
    public List<DietHistoryDTO> getDietHistory(Long userNo) {
        log.info(this.getClass().getName() + ".getDietHistory Start!");
        return dietInfoRepository.findAllByUserIdOrderByCreatedAtDesc(userNo)
                .stream()
                .map(DietHistoryDTO::from)
                .collect(Collectors.toList());
    }

    @Override
    public List<AiReportHistoryDTO> getAiReportHistory(Long userNo) {
        log.info(this.getClass().getName() + ".getAiReportHistory Start!");
        return aiReportRepository.findAllByUserIdOrderByCreatedAtDesc(userNo)
                .stream()
                .map(AiReportHistoryDTO::from)
                .collect(Collectors.toList());
    }
}
