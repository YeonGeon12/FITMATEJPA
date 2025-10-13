package kopo.fitmate.report.service.impl;

import kopo.fitmate.global.ai.OpenAiApiClient;
import kopo.fitmate.report.dto.ReportRequestDTO;
import kopo.fitmate.report.dto.ReportResponseDTO;
import kopo.fitmate.report.service.IReportService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReportService implements IReportService {

    private final OpenAiApiClient openAiApiClient;

    @Override
    public ReportResponseDTO getReport(ReportRequestDTO requestDTO) throws Exception {
        log.info("{}.getReport Start!", getClass().getName());

        // 1. AI 클라이언트를 호출하여 JSON 형태의 분석 리포트를 받음
        ReportResponseDTO responseDTO = openAiApiClient.chatJson(
                "You are a professional health analyst and personal trainer.", // AI에게 '건강 분석가 겸 트레이너' 역할을 부여
                buildReportPrompt(requestDTO),      // 아래에서 생성한 맞춤형 프롬프트를 전달
                ReportResponseDTO.class             // 결과를 ReportResponseDTO 객체로 변환하도록 지정
        );

        log.info("{}.getReport End!", getClass().getName());
        return responseDTO;
    }

    /**
     * AI에게 보낼 프롬프트(명령어)를 생성하는 메서드
     */
    private String buildReportPrompt(ReportRequestDTO dto) {
        return String.format("""
        너는 20년 동안 활동한 베테랑 전문 건강 분석가이자 퍼스널 트레이너다. 아래 사용자 정보를 바탕으로 신체 분석 리포트를 작성해라.
        리포트는 반드시 **JSON 형식**으로만 출력해야 한다.

        [사용자 정보]
        - 키: %d cm
        - 체중: %d kg
        - 나이: %d 세
        - 성별: %s
        - 주간 활동량: %s (예: 거의 운동 안함, 가벼운 활동, 보통 활동 등)

        [출력 스키마 - 반드시 이 구조를 정확히 지켜라]
        {
          "summary": "현재 사용자의 상태를 2~3줄로 요약하는 종합 분석 결과",
          "BMI": "계산된 BMI 수치와 그에 대한 평가 (예: '22.5 (정상 체중)')",
          "BMR": "Harris-Benedict 공식을 사용하여 계산된 기초대사량 (BMR) 수치 (예: '1750 kcal')",
          "TDEE": "BMR과 활동량을 바탕으로 계산된 활동대사량(TDEE) 또는 일일 권장 섭취 칼로리 (예: '2400 kcal')",
          "exerciseAdvice": "계산된 지표를 바탕으로 사용자에게 추천하는 구체적인 운동 조언",
          "dietAdvice": "계산된 지표를 바탕으로 사용자에게 추천하는 구체적인 식단 조언"
        }
        
        - 모든 필드를 반드시 포함해야 한다.
        - 수치는 정확하게 계산해서 제공해야 한다.
        """,
                dto.getHeight(), dto.getWeight(), dto.getAge(), dto.getGender(), dto.getActivityLevel()
        );
    }
}