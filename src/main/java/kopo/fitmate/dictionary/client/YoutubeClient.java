package kopo.fitmate.dictionary.client;

import kopo.fitmate.dictionary.dto.YoutubeDTO;
import kopo.fitmate.global.config.YouTubeFeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * YouTube Data API를 호출하기 위한 Feign 클라이언트
 */
@FeignClient(name = "youtubeClient",
        url = "https://www.googleapis.com/youtube/v3",
        configuration = YouTubeFeignConfig.class)
public interface YoutubeClient {

    /**
     * 키워드로 유튜브 동영상을 검색합니다.
     * @param part 필수 파라미터, 'snippet'으로 고정하여 동영상의 기본 정보(제목, 썸네일 등)를 가져옵니다.
     * @param query 검색할 키워드 (예: "스쿼트 운동")
     * @param type 검색할 리소스의 타입, 'video'로 고정합니다.
     * @param maxResults 가져올 검색 결과의 최대 개수
     * @return 검색된 동영상 정보가 담긴 DTO
     */
    @GetMapping("/search")
    YoutubeDTO searchVideos(@RequestParam("part") String part,
                            @RequestParam("q") String query,
                            @RequestParam("type") String type,
                            @RequestParam("maxResults") int maxResults);
}