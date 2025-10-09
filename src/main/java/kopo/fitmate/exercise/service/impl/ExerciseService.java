package kopo.fitmate.exercise.service.impl;


import kopo.fitmate.global.ai.OpenAiApiClient;
import kopo.fitmate.exercise.dto.ExerciseRequestDTO;
import kopo.fitmate.exercise.dto.ExerciseResponseDTO;
import kopo.fitmate.exercise.repository.ExerciseRepository;
import kopo.fitmate.exercise.repository.entity.DailyRoutineEmbed;
import kopo.fitmate.exercise.repository.entity.ExerciseInfoEntity;
import kopo.fitmate.exercise.service.IExerciseService;
import kopo.fitmate.global.config.util.DateUtil;
import kopo.fitmate.user.dto.UserAuthDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class ExerciseService implements IExerciseService {

    private final ExerciseRepository exerciseRepository;
    private final OpenAiApiClient OpenAiApiClient; // TODO: 다음 단계에서 만들 AI 클라이언트를 주입받을 위치

    @Override
    public ExerciseResponseDTO getExerciseRecommendation(ExerciseRequestDTO requestDTO, UserAuthDTO user) throws Exception {
        log.info(this.getClass().getName() + ".getExerciseRecommendation Start!");

        // 1. AI 클라이언트를 통해 운동 루틴 추천 받기
        ExerciseResponseDTO responseDTO = OpenAiApiClient.getRecommendation(requestDTO); // TODO: 실제 AI 연동 시 이 코드를 활성화

        // 2. MongoDB에 추천 결과 저장
        saveExerciseRecommendation(requestDTO, responseDTO, user);

        log.info(this.getClass().getName() + ".getExerciseRecommendation End!");
        return responseDTO;
    }

    /**
     * 추천받은 운동 정보를 MongoDB에 저장하는 메서드
     */
    private void saveExerciseRecommendation(ExerciseRequestDTO requestDTO, ExerciseResponseDTO responseDTO, UserAuthDTO user) {

        ExerciseInfoEntity entity = new ExerciseInfoEntity();
        entity.setUserId(user.getEmail());
        entity.setRegDt(DateUtil.getDateTime("yyyy-MM-dd HH:mm:ss"));

        // 요청 정보 저장
        entity.setHeight(requestDTO.getHeight());
        entity.setWeight(requestDTO.getWeight());
        entity.setGender(requestDTO.getGender());
        entity.setAge(requestDTO.getAge());
        entity.setExerciseLevel(requestDTO.getExerciseLevel());
        entity.setExerciseGoal(requestDTO.getExerciseGoal());
        entity.setWorkoutLocation(requestDTO.getWorkoutLocation());
        entity.setBodyParts(requestDTO.getBodyParts());

        // 추천 결과(주간 루틴)를 DTO -> Embeddable Object로 변환하여 저장
        List<DailyRoutineEmbed> routineToSave = responseDTO.getWeeklyRoutine().stream().map(dto -> {
            DailyRoutineEmbed embed = new DailyRoutineEmbed();
            embed.setDay(dto.getDay());
            embed.setBodyPart(dto.getBodyPart());
            embed.setExerciseName(dto.getExerciseName());
            embed.setSets(dto.getSets());
            embed.setReps(dto.getReps());
            embed.setRestTime(dto.getRestTime());
            return embed;
        }).collect(Collectors.toList());
        entity.setWeeklyRoutine(routineToSave);

        exerciseRepository.save(entity);
    }
}