package kopo.fitmate.diet.repository.entity;

import lombok.Getter;
import lombok.Setter;

/**
 * MongoDB 문서 내부에 '한 끼' 식사 정보를 저장하기 위한 클래스
 */
@Getter
@Setter
public class MealEmbed { // public으로 선언된 별도 파일

    private String time;

    private String menu;

    private String calories;

}