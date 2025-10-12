package kopo.fitmate.history.service.impl;

import kopo.fitmate.diet.repository.DietRepository;
import kopo.fitmate.exercise.repository.ExerciseRepository;
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
}
