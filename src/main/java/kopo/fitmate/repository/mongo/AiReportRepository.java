package kopo.fitmate.repository.mongo;

import kopo.fitmate.repository.mongo.entity.AiReportEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List; // List 임포트 추가

@Repository
public interface AiReportRepository extends MongoRepository<AiReportEntity, String> {

    /**
     * userId를 기준으로 모든 AI 리포트 문서를 삭제합니다.
     */
    void deleteAllByUserId(Long userId);

    /**
     * [추가된 메서드]
     * userId를 기준으로 모든 문서를 생성일(createdAt) 내림차순으로 조회합니다.
     */
    List<AiReportEntity> findAllByUserIdOrderByCreatedAtDesc(Long userId);
}
