package kopo.fitmate.dictionary.controller;

import kopo.fitmate.dictionary.dto.ExerciseDTO;
import kopo.fitmate.dictionary.dto.TranslatedExerciseDTO;
import kopo.fitmate.dictionary.service.IDictionaryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * 운동 사전 관련 웹 요청을 처리하는 컨트롤러
 */
@Slf4j
@Controller
@RequestMapping("/dictionary")
@RequiredArgsConstructor
public class DictionaryController {

    private final IDictionaryService dictionaryService;

    /**
     * 운동 검색 페이지 및 검색 결과를 처리하는 메서드
     * @param name 검색어 (운동 이름)
     * @param muscle 선택한 운동 부위
     * @param model View에 데이터를 전달하기 위한 객체
     * @return 뷰 이름
     */
    @GetMapping("/search")
    public String dictionarySearch(@RequestParam(value = "name", required = false) String name,
                                   @RequestParam(value = "muscle", required = false) String muscle, Model model) {
        log.info("운동 사전 검색 페이지. 이름: {}, 부위: {}", name, muscle);

        try {
            // 이름이나 부위로 검색어가 들어온 경우에만 서비스 호출
            if ((name != null && !name.isEmpty()) || (muscle != null && !muscle.isEmpty())) {
                List<ExerciseDTO> exercises = dictionaryService.searchExercises(name, muscle);
                model.addAttribute("exercises", exercises);
                log.info("검색된 운동 개수: {}", exercises.size());
            }
        } catch (Exception e) {
            log.error("운동 정보 검색 중 오류 발생", e);
            model.addAttribute("error", "정보를 가져오는 데 실패했습니다. 잠시 후 다시 시도해주세요.");
        }

        // 뷰에서 검색어와 선택된 부위를 기억하기 위해 모델에 추가
        model.addAttribute("searchName", name);
        model.addAttribute("selectedMuscle", muscle);

        return "dictionary/dictionarySearch"; // templates/dictionary/dictionarySearch.html
    }

    /**
     * 운동 상세 정보 페이지를 처리하는 메서드
     * @param name 상세 정보를 조회할 운동의 이름
     * @param model View에 데이터를 전달하기 위한 객체
     * @return 뷰 이름
     */
    @GetMapping("/detail")
    public String exerciseDetail(@RequestParam("name") String name, Model model) {
        log.info("운동 상세 페이지 접근: {}", name);
        try {
            // [수정] 반환받는 타입을 TranslatedExerciseDTO로 변경
            TranslatedExerciseDTO exerciseDetail = dictionaryService.getExerciseDetail(name);

            if (exerciseDetail != null) {
                // [수정] 모델에 'exerciseDetail'이라는 이름으로 담음
                model.addAttribute("exerciseDetail", exerciseDetail);
                // [수정] 유튜브 검색 시 원본 영어 이름으로 검색
                model.addAttribute("youtube", dictionaryService.searchYoutube(exerciseDetail.getOriginal().getName()));
            } else {
                model.addAttribute("error", "해당 운동에 대한 상세 정보를 찾을 수 없습니다.");
            }
        } catch (Exception e) {
            log.error("운동 상세 정보 조회 중 오류 발생", e);
            model.addAttribute("error", "정보를 조회하는 중 오류가 발생했습니다.");
        }
        return "dictionary/exerciseDetail";
    }
}