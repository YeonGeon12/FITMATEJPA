package kopo.fitmate.dictionary.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * API Ninjas의 Exercise API 응답 데이터를 담는 DTO
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true) // JSON 응답에 DTO에 없는 필드가 있어도 무시
public class ExerciseDTO {

    // @JsonProperty는 JSON의 키와 Java 필드명이 다를 경우 매핑해주는 역할
    @JsonProperty("name")
    private String name; // 운동 이름

    @JsonProperty("type")
    private String type; // 운동 타입 (예: 근력)

    @JsonProperty("muscle")
    private String muscle; // 주동근

    @JsonProperty("equipment")
    private String equipment; // 필요 장비

    @JsonProperty("difficulty")
    private String difficulty; // 난이도

    @JsonProperty("instructions")
    private String instructions; // 운동 방법 설명

}
