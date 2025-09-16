package kopo.fitmate.repository.mongo;

import kopo.fitmate.repository.mongo.entity.AiReportEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AiReportRepository extends MongoRepository<AiReportEntity, String> {
    // userId를 기준으로 모든 문서를 삭제
    void deleteAllByUserId(Long userId);
}


