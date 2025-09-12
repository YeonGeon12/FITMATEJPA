package kopo.fitmate.dto.user;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 * Controller에서 View로 사용자 정보를 전달하기 위한 DTO
 * 비밀번호와 같은 민감 정보를 제외하고 화면에 필요한 데이터만 포함
 */
@Getter
@Setter
@Builder
public class UserInfoDTO {

    // 사용자 이름
    private String userName;

    // 사용자 이메일
    private String email;

}
