package kopo.fitmate;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

@EnableRedisHttpSession
@SpringBootApplication
@EnableJpaRepositories(basePackages = "kopo.fitmate.user.repository")
@EnableMongoRepositories(basePackages = {"kopo.fitmate.exercise.repository", "kopo.fitmate.history.repository"})
public class FitmateApplication {

    public static void main(String[] args) {
        SpringApplication.run(FitmateApplication.class, args);
    }

}
