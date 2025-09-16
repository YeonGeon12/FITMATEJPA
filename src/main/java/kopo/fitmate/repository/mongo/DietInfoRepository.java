package kopo.fitmate.repository.mongo;

import kopo.fitmate.repository.mongo.entity.DietInfoEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DietInfoRepository extends MongoRepository<DietInfoEntity, String> {

    /**
     * userId를 기준으로 모든 식단 정보 문서를 삭제합니다.
     */
    void deleteAllByUserId(Long userId);

}


