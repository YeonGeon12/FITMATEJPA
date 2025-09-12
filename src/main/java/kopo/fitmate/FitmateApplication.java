package kopo.fitmate;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

@EnableRedisHttpSession
@SpringBootApplication
public class FitmateApplication {

    public static void main(String[] args) {
        SpringApplication.run(FitmateApplication.class, args);
    }

}
