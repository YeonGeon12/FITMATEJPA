package kopo.fitmate.diet.repository;

import kopo.fitmate.diet.repository.entity.DietInfoEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

/**
 * DietInfoEntity를 MongoDB에서 저장, 조회, 수정, 삭제(CRUD)할 수 있도록 도와주는 인터페이스
 */
@Repository
public interface DietRepository extends MongoRepository<DietInfoEntity, String> {

}
