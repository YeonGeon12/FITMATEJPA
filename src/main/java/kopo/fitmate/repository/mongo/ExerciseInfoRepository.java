package kopo.fitmate.repository.mongo;

import kopo.fitmate.repository.mongo.entity.ExerciseInfoEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ExerciseInfoRepository extends MongoRepository<ExerciseInfoEntity, String> {
    // userId를 기준으로 모든 문서를 삭제
    void deleteAllByUserId(Long userId);
}
    