package kopo.fitmate.history.service.impl;

import kopo.fitmate.diet.repository.DietRepository;
import kopo.fitmate.exercise.repository.ExerciseRepository;
import kopo.fitmate.history.dto.DietDetailDTO;
import kopo.fitmate.history.dto.ExerciseDetailDTO;
import kopo.fitmate.history.dto.HistoryViewDTO;
import kopo.fitmate.history.dto.HistoryItemDTO;
import kopo.fitmate.history.service.IHistoryService;
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

    @Override
    public HistoryViewDTO getHistoryList(UserAuthDTO user) {
        log.info("{}.getHistoryList Start!", getClass().getName());

        // 현재 로그인한 사용자의 아이디 가져오기
        String userId = user.getUsername();

        // 1. 운동 기록 조회 및 DTO로 변환
        // findByUserIdOrderByRegDtDesc 메서드를 호출하여 해당 사용자의 모든 운동 기록을 최신순으로 가져온다.
        var exerciseList = exerciseRepository.findByUserIdOrderByRegDtDesc(userId).stream()
                .map(entity -> HistoryItemDTO.builder() // 각 Entity를 HistoryItemDTO로 변환한다.
                        .id(entity.getId())
                        .type("운동 루틴")
                        .regDt(entity.getRegDt())
                        .summary(entity.getExerciseGoal()) // 운동 목표를 요약 정보로 사용
                        .build())
                .collect(Collectors.toList()); // 변환된 DTO들을 List 형태로 수집

        // 2. 식단 기록 조회 및 DTO로 변환
        // findByUserIdOrderByRegDtDesc 메서드를 호출하여 해당 사용자의 모든 식단 기록을 최신순으로 가져옵니다.
        var dietList = dietRepository.findByUserIdOrderByRegDtDesc(userId).stream()
                .map(entity -> HistoryItemDTO.builder() // 각 Entity를 HistoryItemDTO로 변환하기
                        .id(entity.getId())
                        .type("식단")
                        .regDt(entity.getRegDt())
                        .summary(entity.getDietType()) // 식단 유형을 요약 정보로 사용
                        .build())
                .collect(Collectors.toList()); // 변환된 DTO들을 List 형태로 수집하기

        log.info("{}.getHistoryList End!", getClass().getName());

        // 3. 두 리스트를 하나의 HistoryViewDTO에 담아 반환하기
        return HistoryViewDTO.builder()
                .exerciseList(exerciseList)
                .dietList(dietList)
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

}
