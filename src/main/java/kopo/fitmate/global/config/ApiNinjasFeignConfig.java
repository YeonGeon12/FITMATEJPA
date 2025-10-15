package kopo.fitmate.global.config;

import feign.Logger;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;

// 주의: 이 클래스에는 @Configuration 어노테이션을 붙이지 않습니다.
// 붙이게 되면 모든 Feign Client에 이 설정이 적용되어 버립니다.
public class ApiNinjasFeignConfig {

    @Value("${api.ninjas.key}")
    private String apiKey;

    @Bean
    public RequestInterceptor requestInterceptor() {
        return new RequestInterceptor() {
            @Override
            public void apply(RequestTemplate template) {
                // 모든 요청 헤더에 X-Api-Key와 발급받은 키 값을 추가합니다.
                template.header("X-Api-Key", apiKey);
            }
        };
    }

    @Bean
    Logger.Level feignLoggerLevel() {

        /*
        OpenFeign 통해 전송 및 전달받는 모든 과정에 대해 로그 찍기 설정

        NONE: 로깅하지 않음(기본값)
        BASIC: 요청 메소드와 URI와 응답 상태와 실행시간 로깅함
        HEADERS: 요청과 응답 헤더와 함께 기본 정보들을 남김
        FULL: 요청과 응답에 대한 헤더와 바디, 메타 데이터를 남김
        */
        return Logger.Level.FULL;
    }
}