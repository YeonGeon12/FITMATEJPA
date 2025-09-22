package kopo.fitmate.exercise.service;

import kopo.fitmate.exercise.dto.ExerciseRequestDTO;

public interface IExerciseAiService {

    /**
     * Gemini API를 호출하여 운동 추천 루틴을 받아오는 메소드
     *
     * @param pDTO 사용자가 입력한 운동 정보
     * @return AI가 생성한 운동 추천 결과 (JSON 문자열)
     */
    String getExerciseRecommendation(ExerciseRequestDTO pDTO) throws Exception;
}
