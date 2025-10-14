package kopo.fitmate.history.service.impl;

import kopo.fitmate.diet.repository.DietRepository;
import kopo.fitmate.exercise.repository.ExerciseRepository;
import kopo.fitmate.report.repository.ReportRepository;
import kopo.fitmate.history.dto.DietDetailDTO;
import kopo.fitmate.history.dto.ExerciseDetailDTO;
import kopo.fitmate.history.dto.HistoryViewDTO;
import kopo.fitmate.history.dto.HistoryItemDTO;
import kopo.fitmate.history.service.IHistoryService;
import kopo.fitmate.report.repository.entity.ReportInfoEntity;
import kopo.fitmate.user.dto.UserAuthDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

/**
 * IHistoryService 인터페이스의 구현체
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class HistoryService implements IHistoryService {

    // DB에 저장된 운동 및 식단 기록에 접근하기 위한 Repository 주입하기
    private final ExerciseRepository exerciseRepository;
    private final DietRepository dietRepository;
    private final ReportRepository reportRepository;

    @Override
    public HistoryViewDTO getHistoryList(UserAuthDTO user) {
        log.info("{}.getHistoryList Start!", getClass().getName());

        String userId = user.getUsername();

        // 1. 운동 기록 조회 및 DTO로 변환 (기존과 동일)
        var exerciseList = exerciseRepository.findByUserIdOrderByRegDtDesc(userId).stream()
                .map(entity -> HistoryItemDTO.builder()
                        .id(entity.getId())
                        .type("운동 루틴")
                        .regDt(entity.getRegDt())
                        .summary(entity.getExerciseGoal())
                        .build())
                .collect(Collectors.toList());

        // 2. 식단 기록 조회 및 DTO로 변환 (기존과 동일)
        var dietList = dietRepository.findByUserIdOrderByRegDtDesc(userId).stream()
                .map(entity -> HistoryItemDTO.builder()
                        .id(entity.getId())
                        .type("식단")
                        .regDt(entity.getRegDt())
                        .summary(entity.getDietType())
                        .build())
                .collect(Collectors.toList());

        // 3. AI 리포트 기록 조회 및 DTO로 변환 (신규 추가)
        var reportList = reportRepository.findByUserIdOrderByRegDtDesc(userId).stream()
                .map(entity -> HistoryItemDTO.builder()
                        .id(entity.getId())
                        .type("AI 리포트")
                        .regDt(entity.getRegDt())
                        .summary(entity.getRegDt().substring(0, 10) + " 신체 분석 리포트") // "YYYY.MM.DD 신체 분석 리포트" 형식으로 요약 생성
                        .build())
                .collect(Collectors.toList());

        log.info("{}.getHistoryList End!", getClass().getName());

        // 4. 세 가지 리스트를 하나의 HistoryViewDTO에 담아 반환
        return HistoryViewDTO.builder()
                .exerciseList(exerciseList)
                .dietList(dietList)
                .reportList(reportList) // reportList 추가
                .build();
    }


    @Override
    public ExerciseDetailDTO getExerciseDetail(String id, UserAuthDTO user) {
        log.info("{}.getExerciseDetail Start!", getClass().getName());

        // 1. ID로 DB에서 운동 기록을 찾습니다. 없으면 null을 반환합니다.
        return exerciseRepository.findById(id)
                // 2. 찾은 기록의 소유자(userId)가 현재 로그인한 사용자와 일치하는지 확인합니다.
                .filter(entity -> entity.getUserId().equals(user.getUsername()))
                // 3. 일치한다면, Entity를 ExerciseDetailDTO로 변환합니다.
                .map(entity -> ExerciseDetailDTO.builder()
                        .id(entity.getId())
                        .regDt(entity.getRegDt())
                        .exerciseGoal(entity.getExerciseGoal())
                        .exerciseLevel(entity.getExerciseLevel())
                        .workoutLocation(entity.getWorkoutLocation())
                        .weeklyRoutine(entity.getWeeklyRoutine())
                        .build())
                // 4. 소유자가 아니거나 기록이 없으면 null을 반환합니다.
                .orElse(null);
    }

    @Override
    public DietDetailDTO getDietDetail(String id, UserAuthDTO user) {
        log.info("{}.getDietDetail Start!", getClass().getName());

        return dietRepository.findById(id)
                .filter(entity -> entity.getUserId().equals(user.getUsername()))
                .map(entity -> DietDetailDTO.builder()
                        .id(entity.getId())
                        .regDt(entity.getRegDt())
                        .dietType(entity.getDietType())
                        .weeklyDiet(entity.getWeeklyDiet())
                        .build())
                .orElse(null);
    }

    /**
     *  AI 리포트 상세 정보 조회 구현
     */
    @Override
    public ReportInfoEntity getReportDetail(String id, UserAuthDTO user) {
        log.info("{}.getReportDetail Start!", getClass().getName());
        // ID로 리포트를 찾고, 찾은 리포트의 소유자가 현재 로그인한 사용자인지 확인
        return reportRepository.findById(id)
                .filter(entity -> entity.getUserId().equals(user.getUsername()))
                .orElse(null); // 조건에 맞으면 Entity를, 아니면 null을 반환
    }

    @Override
    public boolean deleteExerciseHistory(String id, UserAuthDTO user) {
        log.info("{}.deleteExerciseHistory Start!", getClass().getName());

        // 1. ID로 DB에서 운동 기록을 찾습니다.
        var entityOptional = exerciseRepository.findById(id);

        // 2. 기록이 존재하고, 소유자가 현재 로그인한 사용자와 일치하는지 확인합니다.
        if (entityOptional.isPresent() && entityOptional.get().getUserId().equals(user.getUsername())) {
            // 3. 조건이 모두 맞으면, 해당 기록을 삭제합니다.
            exerciseRepository.deleteById(id);
            log.info("Exercise history deleted successfully. ID: {}", id);
            return true; // 삭제 성공
        }

        log.warn("Failed to delete exercise history. ID: {} not found or permission denied.", id);
        return false; // 삭제 실패
    }

    @Override
    public boolean deleteDietHistory(String id, UserAuthDTO user) {
        log.info("{}.deleteDietHistory Start!", getClass().getName());

        // 1. ID로 DB에서 식단 기록을 찾습니다.
        var entityOptional = dietRepository.findById(id);

        // 2. 기록이 존재하고, 소유자가 현재 로그인한 사용자와 일치하는지 확인합니다.
        if (entityOptional.isPresent() && entityOptional.get().getUserId().equals(user.getUsername())) {
            // 3. 조건이 모두 맞으면, 해당 기록을 삭제합니다.
            dietRepository.deleteById(id);
            log.info("Diet history deleted successfully. ID: {}", id);
            return true; // 삭제 성공
        }

        log.warn("Failed to delete diet history. ID: {} not found or permission denied.", id);
        return false; // 삭제 실패
    }

    /**
     * [신규 추가] AI 리포트 삭제 구현
     */
    @Override
    public boolean deleteReportHistory(String id, UserAuthDTO user) {
        log.info("{}.deleteReportHistory Start!", getClass().getName());
        var entityOptional = reportRepository.findById(id);
        if (entityOptional.isPresent() && entityOptional.get().getUserId().equals(user.getUsername())) {
            reportRepository.deleteById(id);
            log.info("리포트가 정상적으로 삭제되었습니다. ID: {}", id);
            return true;
        }
        log.warn("Failed to delete report history. ID: {} not found or permission denied.", id);
        return false;
    }

}
