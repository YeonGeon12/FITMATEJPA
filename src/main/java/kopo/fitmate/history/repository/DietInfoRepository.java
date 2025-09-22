package kopo.fitmate.history.repository;

import kopo.fitmate.history.repository.entity.DietInfoEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List; // List 임포트 추가

@Repository
public interface DietInfoRepository extends MongoRepository<DietInfoEntity, String> {

    /**
     * userId를 기준으로 모든 식단 정보 문서를 삭제합니다.
     */
    void deleteAllByUserId(Long userId);

    /**
     * [추가된 메서드]
     * userId를 기준으로 모든 문서를 생성일(createdAt) 내림차순으로 조회합니다.
     */
    List<DietInfoEntity> findAllByUserIdOrderByCreatedAtDesc(Long userId);

}


