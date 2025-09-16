package kopo.fitmate.repository.mongo;

import kopo.fitmate.repository.mongo.entity.AiReportEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AiReportRepository extends MongoRepository<AiReportEntity, String> {

    /**
     * userId를 기준으로 모든 AI 리포트 문서를 삭제합니다.
     */
    void deleteAllByUserId(Long userId);

}


