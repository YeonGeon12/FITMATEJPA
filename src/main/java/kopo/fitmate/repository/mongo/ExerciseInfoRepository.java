package kopo.fitmate.repository.mongo;

import kopo.fitmate.repository.mongo.entity.ExerciseInfoEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List; // List 임포트 추가

@Repository
public interface ExerciseInfoRepository extends MongoRepository<ExerciseInfoEntity, String> {

    /**
     * userId를 기준으로 모든 운동 정보 문서를 삭제합니다.
     * Spring Data MongoDB가 메서드 이름을 분석하여 쿼리를 자동 생성합니다.
     */
    void deleteAllByUserId(Long userId);

    /**
     * [추가된 메서드]
     * userId를 기준으로 모든 문서를 생성일(createdAt) 내림차순으로 조회합니다.
     */
    List<ExerciseInfoEntity> findAllByUserIdOrderByCreatedAtDesc(Long userId);
}