package kopo.fitmate.dictionary.client;

import kopo.fitmate.dictionary.dto.ExerciseDTO;
import kopo.fitmate.global.config.ApiNinjasFeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * API Ninjas의 Exercise API를 호출하기 위한 Feign 클라이언트
 */
@FeignClient(name = "apiNinjasClient",
        url = "https://api.api-ninjas.com/v1",
        configuration = ApiNinjasFeignConfig.class)
public interface ApiNinjasClient {

    /**
     * 운동 이름 또는 근육 부위로 운동 정보를 검색합니다.
     * @param name 검색할 운동 이름 (선택 사항)
     * @param muscle 검색할 근육 부위 (선택 사항)
     * @return 검색된 운동 정보 리스트
     */
    @GetMapping("/exercises")
    List<ExerciseDTO> getExercises(@RequestParam(value = "name", required = false) String name,
                                   @RequestParam(value = "muscle", required = false) String muscle);
}