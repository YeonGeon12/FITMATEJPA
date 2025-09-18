package kopo.fitmate.service.impl;

import kopo.fitmate.dto.history.AiReportHistoryDTO;
import kopo.fitmate.dto.history.DietHistoryDTO;
import kopo.fitmate.dto.history.ExerciseHistoryDTO;
import kopo.fitmate.repository.mongo.AiReportRepository;
import kopo.fitmate.repository.mongo.DietInfoRepository;
import kopo.fitmate.repository.mongo.ExerciseInfoRepository;
import kopo.fitmate.repository.mongo.entity.AiReportEntity;
import kopo.fitmate.repository.mongo.entity.DietInfoEntity;
import kopo.fitmate.repository.mongo.entity.ExerciseInfoEntity;
import kopo.fitmate.service.IHistoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
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

    // --- [추가] 상세 조회 구현 ---
    @Override
    public ExerciseInfoEntity getExerciseDetail(String id) {
        log.info(this.getClass().getName() + ".getExerciseDetail Start!");
        // ID로 데이터를 찾고, 없으면 null 반환 (또는 예외 처리)
        Optional<ExerciseInfoEntity> entity = exerciseInfoRepository.findById(id);
        return entity.orElse(null);
    }

    @Override
    public DietInfoEntity getDietDetail(String id) {
        log.info(this.getClass().getName() + ".getDietDetail Start!");
        Optional<DietInfoEntity> entity = dietInfoRepository.findById(id);
        return entity.orElse(null);
    }

    @Override
    public AiReportEntity getAiReportDetail(String id) {
        log.info(this.getClass().getName() + ".getAiReportDetail Start!");
        Optional<AiReportEntity> entity = aiReportRepository.findById(id);
        return entity.orElse(null);
    }

    // --- [추가] 삭제 구현 ---
    @Override
    public void deleteExerciseHistory(String id) {
        log.info(this.getClass().getName() + ".deleteExerciseHistory Start!");
        exerciseInfoRepository.deleteById(id);
    }

    @Override
    public void deleteDietHistory(String id) {
        log.info(this.getClass().getName() + ".deleteDietHistory Start!");
        dietInfoRepository.deleteById(id);
    }

    @Override
    public void deleteAiReportHistory(String id) {
        log.info(this.getClass().getName() + ".deleteAiReportHistory Start!");
        aiReportRepository.deleteById(id);
    }
}
