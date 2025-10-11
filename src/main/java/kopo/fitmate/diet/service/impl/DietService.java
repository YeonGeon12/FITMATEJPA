package kopo.fitmate.diet.service.impl;

import kopo.fitmate.diet.dto.DietRequestDTO;
import kopo.fitmate.diet.dto.DietResponseDTO;
import kopo.fitmate.user.dto.UserAuthDTO;
import kopo.fitmate.diet.repository.DietRepository;
import kopo.fitmate.diet.repository.entity.DietInfoEntity;
import kopo.fitmate.diet.repository.entity.MealEmbed;
import kopo.fitmate.diet.service.IDietService;
import kopo.fitmate.global.ai.OpenAiApiClient;
import kopo.fitmate.global.config.util.DateUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors; // Collectors import

@Slf4j
@RequiredArgsConstructor
@Service
public class DietService implements IDietService {

    // OpenAI API와 통신하기 위한 클라이언트
    private final OpenAiApiClient openAiApiClient;
    // 저장을 위한 Repository 가져오기
    private final DietRepository dietRepository;

    @Override
    public DietResponseDTO getDietRecommendation(DietRequestDTO requestDTO) throws Exception {
        log.info("{}.getDietRecommendation Start!", getClass().getName());

        // 1. AI 클라이언트를 호출하여 JSON 형태의 식단 추천 결과를 받음
        DietResponseDTO responseDTO = openAiApiClient.chatJson(
                "You are a professional nutritionist.", // AI에게 '전문 영양사' 역할을 부여
                buildDietPrompt(requestDTO),             // 아래에서 생성한 맞춤형 프롬프트를 전달
                DietResponseDTO.class                    // 결과를 DietResponseDTO 객체로 변환하도록 지정
        );

        log.info("{}.getDietRecommendation End!", getClass().getName());
        return responseDTO;
    }

    /**
     * AI에게 보낼 프롬프트(명령어)를 생성하는 메서드
     */
    private String buildDietPrompt(DietRequestDTO dto) {
        // String.format을 사용하여 사용자의 요청(dto.getDietType())을 프롬프트에 동적으로 삽입
        return String.format("""
        너는 20년 경력의 전문 영양사다. 아래 정보를 바탕으로 1주(월~일) 식단을 추천해라.
        아침, 점심, 저녁을 모두 포함해야 한다. 반드시 **JSON만** 출력한다.

        [요청]
        - 식단 유형: %s

        [출력 스키마 - 정확히 지켜라]
        {
          "weeklyDiet": [
            { "day":"월", "mealTime":"아침", "menu":"닭가슴살 샐러드", "calories":"350kcal" },
            { "day":"월", "mealTime":"점심", "menu":"현미밥과 된장찌개", "calories":"550kcal" }
          ]
        }
        """, dto.getDietType());
    }

    /**
     * 추천받은 식단을 MongoDB에 저장하는 메서드
     */
    @Override
    public void saveDietRecommendation(DietRequestDTO requestDTO, DietResponseDTO responseDTO, UserAuthDTO user) {
        log.info("{}.saveDietRecommendation Start!", getClass().getName());

        // 1. DB에 저장할 Entity 객체 생성
        DietInfoEntity entity = new DietInfoEntity();

        // 2. 기본 정보 설정 (사용자 ID, 저장 시간, 식단 유형)
        entity.setUserId(user.getUsername());
        entity.setRegDt(DateUtil.getDateTime());
        entity.setDietType(requestDTO.getDietType());

        // 3. 주간 식단 정보 설정 (DTO -> Embed 객체로 변환하여 리스트에 담기)
        entity.setWeeklyDiet(responseDTO.getWeeklyDiet().stream().map(dto -> {
            MealEmbed embed = new MealEmbed();
            embed.setDay(dto.getDay());
            embed.setMealTime(dto.getMealTime());
            embed.setMenu(dto.getMenu());
            embed.setCalories(dto.getCalories());
            return embed;
        }).collect(Collectors.toList()));

        // 4. Repository를 통해 DB에 저장
        dietRepository.save(entity);

        log.info("{}.saveDietRecommendation End!", getClass().getName());
    }
}