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
    private final OpenAiApiClient openAiApiClient;

    @Override
    public ExerciseResponseDTO getExerciseRecommendation(ExerciseRequestDTO requestDTO, UserAuthDTO user) throws Exception {
        log.info("{}.getExerciseRecommendation Start!", getClass().getName());

        // 1) OpenAI 호출 (JSON 강제 + DTO 역직렬화)
        ExerciseResponseDTO responseDTO = openAiApiClient.chatJson(
                "You are a professional fitness trainer.",
                buildExercisePrompt(requestDTO),
                ExerciseResponseDTO.class
        );

        // 2) DB 저장 로직 삭제됨

        log.info("{}.getExerciseRecommendation End!", getClass().getName());
        return responseDTO; // AI가 생성한 결과만 바로 반환
    }

    /** 운동 추천 프롬프트 (DTO 스키마에 맞게 JSON만 출력하도록 강제) */
    private String buildExercisePrompt(ExerciseRequestDTO dto) {
        return String.format("""
        너는 전문 트레이너다. 아래 정보를 바탕으로 1주(월~일) 운동 루틴을 추천해라.
        반드시 **JSON만** 출력한다.

        [사용자]
        - 키:%dcm, 체중:%dkg, 성별:%s, 나이:%d세
        - 운동 수준:%s, 목표:%s, 운동 장소:%s
        - 집중 부위:%s

        [출력 스키마 - 정확히 지켜라]
        {
          "weeklyRoutine": [
            { "day":"월", "bodyPart":"가슴", "exerciseName":"푸쉬업",
              "sets":"4", "reps":"12-15", "restTime":"60초" }
          ]
        }

        - 월~일 모두 포함
        - bodyPart='휴식'인 날은 exerciseName/sets/reps/restTime은 "-"로 채운다.
        """,
                dto.getHeight(), dto.getWeight(), dto.getGender(), dto.getAge(),
                dto.getExerciseLevel(), dto.getExerciseGoal(), dto.getWorkoutLocation(),
                dto.getBodyParts() != null ? String.join(", ", dto.getBodyParts()) : "-"
        );
    }

    /**
     * 추천 결과 저장 (private -> public 변경 및 @Override 추가)
     */
    @Override
    public void saveExerciseRecommendation(ExerciseRequestDTO requestDTO, ExerciseResponseDTO responseDTO, UserAuthDTO user) {
        log.info("{}.saveExerciseRecommendation Start!", getClass().getName());

        ExerciseInfoEntity entity = new ExerciseInfoEntity();

        entity.setUserId(user.getUsername());
        entity.setRegDt(DateUtil.getDateTime());

        entity.setHeight(requestDTO.getHeight());
        entity.setWeight(requestDTO.getWeight());
        entity.setGender(requestDTO.getGender());
        entity.setAge(requestDTO.getAge());
        entity.setExerciseLevel(requestDTO.getExerciseLevel());
        entity.setExerciseGoal(requestDTO.getExerciseGoal());
        entity.setWorkoutLocation(requestDTO.getWorkoutLocation());
        entity.setBodyParts(requestDTO.getBodyParts());

        List<DailyRoutineEmbed> routineToSave = responseDTO.getWeeklyRoutine().stream()
                .map(dto -> {
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
        log.info("{}.saveExerciseRecommendation End!", getClass().getName());
    }
}
