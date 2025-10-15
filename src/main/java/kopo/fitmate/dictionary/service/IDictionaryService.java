package kopo.fitmate.dictionary.service;

import kopo.fitmate.dictionary.dto.ExerciseDTO;
import kopo.fitmate.dictionary.dto.YoutubeDTO;
import kopo.fitmate.dictionary.dto.TranslatedExerciseDTO;

import java.util.List;

/**
 * 운동 사전 기능의 비즈니스 로직을 정의하는 인터페이스
 */
public interface IDictionaryService {

    /**
     * 운동 이름 또는 부위로 운동 목록을 검색합니다.
     * @param name 검색할 운동 이름 (선택 사항)
     * @param muscle 검색할 근육 부위 (선택 사항)
     * @return 검색된 운동 정보 리스트
     */
    List<TranslatedExerciseDTO> searchExercises(String name, String muscle);

    /**
     * 특정 운동 이름으로 상세 정보를 조회합니다.
     * @param name 조회할 운동의 정확한 이름
     * @return 조회된 운동의 상세 정보 DTO
     */
    TranslatedExerciseDTO getExerciseDetail(String name) throws Exception;

    /**
     * 주어진 검색어로 YouTube 동영상을 검색합니다.
     * @param query 검색어
     * @return 검색된 YouTube 동영상 정보 DTO
     */
    YoutubeDTO searchYoutube(String query);


}