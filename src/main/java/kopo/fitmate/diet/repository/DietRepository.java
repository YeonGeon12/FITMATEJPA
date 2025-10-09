package kopo.fitmate.diet.repository;

import kopo.fitmate.diet.repository.entity.DietInfoEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;


/**
 * MongoDB의 'DIET_INFO' 컬렉션에 접근하기 위한 Repository 인터페이스
 * Spring Data MongoDB가 이 인터페이스를 기반으로 실제 DB처리 로직 자동 생성
 */
@Repository
public interface DietRepository extends MongoRepository<DietInfoEntity, String> {

}
