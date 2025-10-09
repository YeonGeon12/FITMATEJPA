package kopo.fitmate.diet.service.impl;

import kopo.fitmate.diet.dto.DietRequestDTO;
import kopo.fitmate.diet.dto.DietResponseDTO;
import kopo.fitmate.diet.repository.DietRepository;
import kopo.fitmate.diet.repository.entity.DailyDietEmbed;
import kopo.fitmate.diet.repository.entity.DietInfoEntity;
import kopo.fitmate.diet.repository.entity.MealEmbed;
import kopo.fitmate.diet.service.IDietService;
import kopo.fitmate.global.ai.OpenAiApiClient; // 운동 추천 때 만든 OpenAiService를 재사용
import kopo.fitmate.global.config.util.DateUtil;
import kopo.fitmate.user.dto.UserAuthDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class DietService implements IDietService {

    private final DietRepository dietRepository;
    private final OpenAiApiClient openAiService; // AI 클라이언트 주입

    @Override
    public DietResponseDTO getDietRecommendation(DietRequestDTO requestDTO, UserAuthDTO user) throws Exception {
        log.info(this.getClass().getName() + ".getDietRecommendation Start!");

        // 1. 식단 추천을 위한 새로운 프롬프트 생성
        String prompt = createDietPrompt(requestDTO);

        // 2. OpenAiService를 호출하여 AI로부터 응답 받기
        DietResponseDTO responseDTO = openAiService.getDietRecommendation(prompt); // OpenAiService에 새 메서드 추가 필요

        // 3. 추천받은 식단 정보를 MongoDB에 저장
        saveDietRecommendation(requestDTO, responseDTO, user);

        log.info(this.getClass().getName() + ".getDietRecommendation End!");
        return responseDTO;
    }

    /**
     * 추천받은 식단 정보를 MongoDB에 저장하는 메서드
     */
    private void saveDietRecommendation(DietRequestDTO requestDTO, DietResponseDTO responseDTO, UserAuthDTO user) {

        DietInfoEntity entity = new DietInfoEntity();
        entity.setUserId(user.getEmail());
        entity.setRegDt(DateUtil.getDateTime("yyyy-MM-dd HH:mm:ss"));

        // 요청 정보 저장
        entity.setHeight(requestDTO.getHeight());
        entity.setWeight(requestDTO.getWeight());
        entity.setGender(requestDTO.getGender());
        entity.setDietGoal(requestDTO.getDietGoal());

        // 응답 정보 저장 (DTO -> Entity Embed 객체로 변환)
        entity.setTotalCalories(responseDTO.getTotalCalories());
        entity.setWeeklyDiet(responseDTO.getWeeklyDiet().stream().map(dailyDietDTO -> {
            DailyDietEmbed dailyDietEmbed = new DailyDietEmbed();
            dailyDietEmbed.setDay(dailyDietDTO.getDay());
            dailyDietEmbed.setMeals(dailyDietDTO.getMeals().stream().map(mealDTO -> {
                MealEmbed mealEmbed = new MealEmbed();
                mealEmbed.setTime(mealDTO.getTime());
                mealEmbed.setMenu(mealDTO.getMenu());
                mealEmbed.setCalories(mealDTO.getCalories());
                return mealEmbed;
            }).collect(Collectors.toList()));
            return dailyDietEmbed;
        }).collect(Collectors.toList()));

        dietRepository.save(entity);
    }

    /**
     * GPT에게 보낼 식단 추천용 프롬프트를 생성하는 메서드
     */
    private String createDietPrompt(DietRequestDTO dto) {
        return String.format(
                "너는 사용자의 건강 데이터를 기반으로 맞춤형 식단을 추천하는 전문 영양사야. " +
                        "아래의 사용자 정보를 바탕으로, 반드시 'JSON 형식'으로만 주간 식단을 생성해줘. " +
                        "어떠한 설명도 없이 JSON 코드 블록만 반환해야 해.\n\n" +
                        "## 사용자 정보:\n" +
                        "- 키: %dcm, 체중: %dkg, 성별: %s, 식단 목표: %s\n\n" +
                        "## 출력 JSON 형식 (매우 중요):\n" +
                        "루트 요소는 'total_calories'(String)와 'weekly_diet'(Array) 키를 가져야 해. " +
                        "'weekly_diet' 배열의 각 요소는 'day'(String)와 'meals'(Array) 키를 가져야 하고, " +
                        "'meals' 배열의 각 요소는 'time'(String), 'menu'(String), 'calories'(String) 키를 모두 포함해야 해.\n\n" +
                        "## 예시 JSON:\n" +
                        "{\n" +
                        "  \"total_calories\": \"약 2000kcal\",\n" +
                        "  \"weekly_diet\": [\n" +
                        "    {\n" +
                        "      \"day\": \"월요일\",\n" +
                        "      \"meals\": [\n" +
                        "        { \"time\": \"아침\", \"menu\": \"현미밥, 된장국, 계란후라이\", \"calories\": \"약 500kcal\" },\n" +
                        "        { \"time\": \"점심\", \"menu\": \"닭가슴살 샐러드, 고구마\", \"calories\": \"약 600kcal\" },\n" +
                        "        { \"time\": \"저녁\", \"menu\": \"연어 구이, 아스파라거스\", \"calories\": \"약 600kcal\" }\n" +
                        "      ]\n" +
                        "    }\n" +
                        "  ]\n" +
                        "}",
                dto.getHeight(), dto.getWeight(), dto.getGender(), dto.getDietGoal()
        );
    }
}