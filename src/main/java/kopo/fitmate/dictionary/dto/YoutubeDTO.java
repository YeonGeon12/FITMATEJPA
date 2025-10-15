package kopo.fitmate.dictionary.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import java.util.List;

/**
 * YouTube Data API의 동영상 검색(Search) 결과를 담는 DTO
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class YoutubeDTO {

    private List<Item> items; // 검색 결과 동영상 리스트

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Item {
        private Id id;
        private Snippet snippet;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Id {
        private String videoId; // 동영상의 고유 ID
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Snippet {
        private String title; // 동영상 제목
        private Thumbnails thumbnails; // 썸네일 이미지 정보
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Thumbnails {
        private Thumbnail high; // 고화질 썸네일
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Thumbnail {
        private String url; // 썸네일 이미지 URL
    }
}