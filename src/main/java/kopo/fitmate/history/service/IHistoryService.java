package kopo.fitmate.history.service;

import kopo.fitmate.history.dto.DietDetailDTO;
import kopo.fitmate.history.dto.ExerciseDetailDTO;
import kopo.fitmate.history.dto.HistoryViewDTO;
import kopo.fitmate.report.repository.entity.ReportInfoEntity;
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

    /**
     * ID를 기준으로 저장된 운동 루틴의 상세 정보를 가져옵니다.
     * @param id 조회할 운동 루틴의 고유 ID
     * @param user 현재 로그인한 사용자 정보 (소유권 확인용)
     * @return 운동 루틴 상세 정보 DTO
     */
    ExerciseDetailDTO getExerciseDetail(String id, UserAuthDTO user);

    /**
     * ID를 기준으로 저장된 식단의 상세 정보를 가져옵니다.
     * @param id 조회할 식단의 고유 ID
     * @param user 현재 로그인한 사용자 정보 (소유권 확인용)
     * @return 식단 상세 정보 DTO
     */
    DietDetailDTO getDietDetail(String id, UserAuthDTO user);

    /**
     * ID를 기준으로 저장된 AI 리포트의 상세 정보를 가져옵니다.
     * @param id 조회할 리포트의 고유 ID
     * @param user 현재 로그인한 사용자 정보 (소유권 확인용)
     * @return 리포트 상세 정보가 담긴 Entity
     */
    ReportInfoEntity getReportDetail(String id, UserAuthDTO user);

    /**
     * ID를 기준으로 저장된 운동 루틴을 삭제합니다.
     * @param id 삭제할 운동 루틴의 고유 ID
     * @param user 현재 로그인한 사용자 정보 (소유권 확인용)
     * @return 삭제 성공 시 true, 실패 시 false
     */
    boolean deleteExerciseHistory(String id, UserAuthDTO user);

    /**
     * ID를 기준으로 저장된 식단을 삭제합니다.
     * @param id 삭제할 식단의 고유 ID
     * @param user 현재 로그인한 사용자 정보 (소유권 확인용)
     * @return 삭제 성공 시 true, 실패 시 false
     */
    boolean deleteDietHistory(String id, UserAuthDTO user);

    /**
     * ID를 기준으로 저장된 AI 리포트를 삭제합니다.
     * @param id 삭제할 리포트의 고유 ID
     * @param user 현재 로그인한 사용자 정보 (소유권 확인용)
     * @return 삭제 성공 시 true, 실패 시 false
     */
    boolean deleteReportHistory(String id, UserAuthDTO user);

}
