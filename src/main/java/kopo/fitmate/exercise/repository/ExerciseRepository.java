package kopo.fitmate.exercise.repository;

import kopo.fitmate.exercise.repository.entity.ExerciseInfoEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * MongoDB의 'EXERCISE_INFO' 컬렉션에 접근하기 위한 Repository 인터페이스
 */
@Repository
public interface ExerciseRepository extends MongoRepository<ExerciseInfoEntity, String> {

    // 사용자 ID를 기준으로 모든 운동 기록을 'regDt' 필드의 내림차순(최신순)으로 조회
    List<ExerciseInfoEntity> findByUserIdOrderByRegDtDesc(String userId);

}