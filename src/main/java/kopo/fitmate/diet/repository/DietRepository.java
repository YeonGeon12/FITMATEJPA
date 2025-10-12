package kopo.fitmate.diet.repository;

import kopo.fitmate.diet.repository.entity.DietInfoEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * DietInfoEntity를 MongoDB에서 저장, 조회, 수정, 삭제(CRUD)할 수 있도록 도와주는 인터페이스
 */
@Repository
public interface DietRepository extends MongoRepository<DietInfoEntity, String> {

    // 사용자 ID를 기준으로 모든 운동 기록을 'regDt' 필드의 내림차순(최신순)으로 조회
    List<DietInfoEntity> findByUserIdOrderByRegDtDesc(String userId);

}
