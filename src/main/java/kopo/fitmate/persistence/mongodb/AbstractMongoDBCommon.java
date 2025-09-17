package kopo.fitmate.persistence.mongodb;

import com.mongodb.client.model.Indexes;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.MongoTemplate;

/**
 * MongoDB 공통 기능을 수행하는 추상 클래스
 * 이 클래스를 상속받아 MongoDB 관련 Repository 또는 Service를 구현합니다.
 */
@Slf4j
public abstract class AbstractMongoDBCommon {

    /**
     * 컬렉션을 삭제하는 메서드
     *
     * @param mongodb 접속된 MongoDB 객체
     * @param colNm   삭제할 컬렉션 이름
     * @return 삭제 성공 여부
     */
    protected boolean dropCollection(MongoTemplate mongodb, String colNm) {
        if (mongodb.collectionExists(colNm)) {
            mongodb.dropCollection(colNm);
            log.info("Collection '{}' dropped successfully.", colNm);
            return true;
        } else {
            log.warn("Collection '{}' does not exist. Skipping drop.", colNm);
            return false;
        }
    }

    /**
     * 인덱스가 없는 컬렉션을 생성하는 메서드
     * 내부적으로 인덱스가 여러 개인 생성 메서드를 호출하여 코드를 재사용합니다.
     *
     * @param mongodb 접속된 MongoDB 객체
     * @param colNm   생성할 컬렉션 이름
     * @return 생성 결과 (true: 새로 생성됨, false: 이미 존재함)
     */
    protected boolean createCollection(MongoTemplate mongodb, String colNm) {
        // 인덱스가 없는 경우, 빈 배열을 전달하여 createCollection(String[] index) 메서드에 위임
        return createCollection(mongodb, colNm, new String[0]);
    }

    /**
     * 단일 인덱스를 가진 컬렉션을 생성하는 메서드
     * 내부적으로 인덱스가 여러 개인 생성 메서드를 호출하여 코드를 재사용합니다.
     *
     * @param mongodb 접속된 MongoDB 객체
     * @param colNm   생성할 컬렉션 이름
     * @param index   생성할 단일 인덱스 필드 이름
     * @return 생성 결과 (true: 새로 생성됨, false: 이미 존재함)
     */
    protected boolean createCollection(MongoTemplate mongodb, String colNm, String index) {
        // 문자열을 배열로 변환하여 createCollection(String[] index) 메서드에 위임
        return createCollection(mongodb, colNm, new String[]{index});
    }

    /**
     * 여러 인덱스를 가진 컬렉션을 생성하는 핵심 메서드 (Main Logic)
     *
     * @param mongodb 접속된 MongoDB 객체
     * @param colNm   생성할 컬렉션 이름
     * @param indexes 생성할 인덱스 필드 이름 배열
     * @return 생성 결과 (true: 새로 생성됨, false: 이미 존재함)
     */
    protected boolean createCollection(MongoTemplate mongodb, String colNm, String[] indexes) {
        log.info("{}.createCollection Start!", this.getClass().getName());

        // 1. 컬렉션 존재 여부 확인
        if (mongodb.collectionExists(colNm)) {
            log.warn("Collection '{}' already exists.", colNm);
            return false; // 이미 존재하므로 false 반환
        }

        // 2. 컬렉션 생성
        mongodb.createCollection(colNm);
        log.info("Collection '{}' created successfully.", colNm);


        // 3. 인덱스 생성 (인덱스 정보가 있을 경우에만)
        if (indexes != null && indexes.length > 0) {
            // MongoDB의 인덱스 생성은 백그라운드에서 처리될 수 있음
            mongodb.getCollection(colNm).createIndex(Indexes.ascending(indexes));
            log.info("Indexes created for collection '{}': {}", colNm, String.join(", ", indexes));
        }

        log.info("{}.createCollection End!", this.getClass().getName());
        return true; // 새로 생성되었으므로 true 반환
    }
}
