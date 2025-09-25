package kopo.fitmate.history.service.impl;

import kopo.fitmate.history.dto.AiReportHistoryDTO;
import kopo.fitmate.history.dto.DietHistoryDTO;
import kopo.fitmate.history.dto.ExerciseHistoryDTO;
import kopo.fitmate.history.service.IHistoryService;
import kopo.fitmate.history.repository.AiReportRepository;
import kopo.fitmate.history.repository.DietInfoRepository;
import kopo.fitmate.history.repository.ExerciseInfoRepository;
import kopo.fitmate.history.repository.entity.AiReportEntity;
import kopo.fitmate.history.repository.entity.DietInfoEntity;
import kopo.fitmate.history.repository.entity.ExerciseInfoEntity;
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

        // 1. DB에서 가져온 원본 데이터(Entity)를 로그로 찍어봅니다.
        List<AiReportEntity> entityList = aiReportRepository.findAllByUserIdOrderByCreatedAtDesc(userNo);
        log.info("DB에서 가져온 AI 리포트 원본 데이터 개수: " + entityList.size());
        entityList.forEach(entity -> {
            log.info(" -> Entity ID: " + entity.getId() + ", Summary: " + entity.getReportSummary());
        });

        // 2. DTO로 변환된 후의 데이터를 로그로 찍어봅니다.
        List<AiReportHistoryDTO> dtoList = entityList.stream()
                .map(AiReportHistoryDTO::from)
                .collect(Collectors.toList());
        log.info("DTO로 변환된 AI 리포트 데이터 개수: " + dtoList.size());
        dtoList.forEach(dto -> {
            log.info(" -> DTO ID: " + dto.getId() + ", Summary: " + dto.getReportSummary());
        });

        log.info(this.getClass().getName() + ".getAiReportHistory End!");
        return dtoList; // 최종적으로 변환된 dtoList를 반환
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

    // --- 운동 저장 구현 ---
    @Override
    public void saveExerciseHistory(ExerciseInfoEntity pEntity) {
        log.info(this.getClass().getName() + ".saveExerciseHistory Start!");
        exerciseInfoRepository.save(pEntity);
        log.info(this.getClass().getName() + ".saveExerciseHistory End!");
    }
}
