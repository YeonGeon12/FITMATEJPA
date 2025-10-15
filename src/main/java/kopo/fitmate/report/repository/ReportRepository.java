package kopo.fitmate.report.repository;

import kopo.fitmate.report.repository.entity.ReportInfoEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * MongoDB의 'REPORT_INFO' 컬렉션에 접근하기 위한 Repository 인터페이스
 */
@Repository
public interface ReportRepository extends MongoRepository<ReportInfoEntity, String> {

    // 사용자 ID를 기준으로 모든 리포트를 최신순으로 조회하는 메서드 (추후 '내 기록 보기'에서 사용)
    List<ReportInfoEntity> findByUserIdOrderByRegDtDesc(String userId);

    // [추가된 코드] 사용자 ID를 기준으로 모든 운동 기록을 삭제
    void deleteAllByUserId(String userId);

}