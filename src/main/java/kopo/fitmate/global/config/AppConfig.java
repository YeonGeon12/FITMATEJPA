package kopo.fitmate.global.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 프로젝트 전반에 사용될 Bean들을 등록하는 설정 클래스
 */
@Configuration
public class AppConfig {

    /**
     * ObjectMapper를 Spring Bean으로 등록
     * @return 싱글톤 ObjectMapper 객체
     */
    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }
}