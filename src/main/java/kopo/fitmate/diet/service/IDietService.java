package kopo.fitmate.diet.service;

import kopo.fitmate.diet.dto.DietRequestDTO;
import kopo.fitmate.diet.dto.DietResponseDTO;
import kopo.fitmate.user.dto.UserAuthDTO;

/**
 * 식단 추천 기능의 비즈니스 로직을 정의하는 인터페이스
 */
public interface IDietService {

    /**
     * 사용자의 식단 유형 요청을 받아 AI에게 식단 추천을 요청하고, 그 결과를 반환하는 메서드
     *
     * @param requestDTO 사용자가 폼에서 선택한 식단 유형 정보
     * @return AI가 추천한 주간 식단
     */
    DietResponseDTO getDietRecommendation(DietRequestDTO requestDTO) throws Exception;


    /**
     * 생성된 식단 추천 결과를 DB에 저장하는 메서드
     * @param requestDTO AI 추천에 사용된 식단 유형 데이터
     * @param responseDTO AI가 생성한 추천 결과 데이터
     * @param user 현재 로그인된 사용자 정보
     */
    void saveDietRecommendation(DietRequestDTO requestDTO, DietResponseDTO responseDTO, UserAuthDTO user);

}
