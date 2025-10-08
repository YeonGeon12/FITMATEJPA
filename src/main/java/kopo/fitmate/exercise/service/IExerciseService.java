package kopo.fitmate.exercise.service;

import kopo.fitmate.exercise.dto.ExerciseRequestDTO;
import kopo.fitmate.exercise.dto.ExerciseResponseDTO;
import kopo.fitmate.user.dto.UserAuthDTO;

/**
 * 운동 추천 기능의 비즈니스 로직을 정의하는 인터페이스
 */
public interface IExerciseService {

    /**
     * 사용자의 요청 정보를 받아 AI에게 운동 추천을 요청하고, 그 결과를 반환하는 메서드
     *
     * @param requestDTO 사용자가 폼에 입력한 정보
     * @param user 현재 로그인한 사용자 정보
     * @return AI가 추천한 주간 운동 루틴
     */
    ExerciseResponseDTO getExerciseRecommendation(ExerciseRequestDTO requestDTO, UserAuthDTO user) throws Exception;
}