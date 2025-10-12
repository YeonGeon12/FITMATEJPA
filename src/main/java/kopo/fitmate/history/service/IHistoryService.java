package kopo.fitmate.history.service;

import kopo.fitmate.history.dto.HistoryViewDTO;
import kopo.fitmate.user.dto.UserAuthDTO;

/**
 * '내 기록 보기' 기능의 비즈니스 로직을 정의하는 인터페이스
 */
public interface IHistoryService {

    /**
     * 현재 로그인된 사용자의 모든 기록(운동, 식단)을 DB에서 조회하여
     * '내 기록 보기' 화면에 맞는 DTO 형태로 가공하여 반환합니다.
     * @param user 현재 로그인된 사용자 정보(이 정보를 통해 userId를 얻을 수 있다)
     * @return 운동 기록 리스트와 식단 기록 리스트가 담긴 HistoryViewDTO
     */
    HistoryViewDTO getHistoryList(UserAuthDTO user);

}
