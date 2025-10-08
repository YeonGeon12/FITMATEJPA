package kopo.fitmate.exercise.repository;

import kopo.fitmate.exercise.repository.entity.ExerciseInfoEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

/**
 * MongoDB의 'EXERCISE_INFO' 컬렉션에 접근하기 위한 Repository 인터페이스
 */
@Repository
public interface ExerciseRepository extends MongoRepository<ExerciseInfoEntity, String> {

}