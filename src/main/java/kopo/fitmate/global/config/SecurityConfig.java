package kopo.fitmate.global.config;

import kopo.fitmate.global.config.util.EncryptUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Slf4j
@Configuration // 이 클래스가 Spring의 설정 파일임을 나타냅니다.
@EnableWebSecurity // Spring Security를 활성화합니다.
@RequiredArgsConstructor
public class SecurityConfig {

    /**
     * 비밀번호 암호화 객체를 Spring의 Bean으로 등록합니다.
     * 이렇게 등록하면 프로젝트의 다른 곳에서 @Autowired나 생성자 주입으로 편리하게 사용할 수 있습니다.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        // 기존에 사용하던 EncryptUtil을 Spring Security의 PasswordEncoder 인터페이스와 통합합니다.
        return new PasswordEncoder() {

            /**
             * 비밀번호를 암호화하는 메서드.
             * @param rawPassword 암호화되지 않은 원본 비밀번호
             * @return SHA-256으로 암호화된 비밀번호
             */
            @Override
            public String encode(CharSequence rawPassword) {
                try {
                    // 기존에 사용하던 암호화 유틸리티를 그대로 활용
                    return EncryptUtil.encHashSHA256(rawPassword.toString());
                } catch (Exception e) {
                    log.error("비밀번호 암호화 중 오류 발생", e);
                    return "";
                }
            }

            /**
             * 입력된 비밀번호와 DB에 저장된 암호화된 비밀번호가 일치하는지 확인하는 메서드.
             * 로그인 시 Spring Security가 이 메서드를 자동으로 호출합니다.
             * @param rawPassword 사용자가 입력한 원본 비밀번호
             * @param encodedPassword DB에 저장된 암호화된 비밀번호
             * @return 일치하면 true, 아니면 false
             */
            @Override
            public boolean matches(CharSequence rawPassword, String encodedPassword) {
                try {
                    String encryptedRawPassword = EncryptUtil.encHashSHA256(rawPassword.toString());
                    return encryptedRawPassword.equals(encodedPassword);
                } catch (Exception e) {
                    log.error("비밀번호 비교 중 오류 발생", e);
                    return false;
                }
            }
        };
    }

    /**
     * AuthenticationManager를 Bean으로 등록하여 다른 곳에서 주입받아 사용할 수 있도록 합니다.
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    /**
     * Spring Security의 세부적인 웹 보안 설정을 구성하는 메서드.
     * @param http HttpSecurity 객체
     * @return 설정이 적용된 SecurityFilterChain 객체
     * @throws Exception 설정 과정에서 발생할 수 있는 예외
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        // CSRF(Cross-Site Request Forgery) 보호 기능을 비활성화합니다.
        // REST API를 사용하는 서버에서는 보통 비활성화하며, 세션 대신 JWT 같은 토큰 방식을 사용할 때 권장됩니다.
        http.csrf(csrf -> csrf.disable());

        // HTTP 요청에 대한 접근 권한을 설정합니다.
        http.authorizeHttpRequests(auth -> auth
                // 아래에 명시된 URL 패턴들은...
                .requestMatchers(
                        "/",                // 루트 경로 허용
                        "/user/loginForm",  // 로그인 페이지 보기
                        "/user/joinForm",   // 회원가입 페이지 보기
                        "/user/join",       // 회원가입 처리 (Thymeleaf 폼 제출)
                        "/user/forgotPasswordForm", // 비밀번호 찾기 페이지 이동
                        "/user/resetPasswordForm", // 비밀번호 재설정 페이지 이동
                        "/api/user/**",     // 모든 사용자 관련 API (이메일 중복 체크, API 방식 회원가입)
                        "/css/**",          // CSS 파일
                        "/js/**",           // JavaScript 파일
                        "/img/**"           // img 파일
                ).permitAll() // ...permitAll() : 로그인 여부와 관계없이 누구나 접근을 '허용'합니다.

                // 위에서 허용한 URL 외의 모든(any) 요청(request)은...
                .anyRequest().authenticated() // ...authenticated() : 반드시 '인증'(로그인)된 사용자만 접근을 허용합니다.
        );

        // 폼 기반 로그인 기능을 설정합니다.
        http.formLogin(login -> login
                // 사용자가 직접 만들 로그인 페이지의 URL을 지정합니다. (나중에 만들어야 함)
                .loginPage("/user/loginForm")

                // Spring Security가 로그인을 처리할 URL을 지정합니다.
                // 이 URL에 대한 POST 요청은 우리가 직접 만들 필요 없이 Spring Security가 가로채서 처리합니다.
                .loginProcessingUrl("/user/loginProc")

                // 로그인 폼에서 아이디(username)에 해당하는 input의 name 속성값을 지정합니다.
                .usernameParameter("email")

                // 로그인 폼에서 비밀번호(password)에 해당하는 input의 name 속성값을 지정합니다.
                .passwordParameter("password")

                // [수정됨] 로그인 성공 시 이동할 URL을 /main에서 /index로 변경
                .defaultSuccessUrl("/index", true)

                // 로그인에 실패했을 때 이동할 URL을 지정합니다.
                .failureUrl("/user/loginForm?error=true") // 실패 시 에러 파라미터와 함께 다시 로그인 페이지로
                .permitAll() // 로그인 페이지는 누구나 접근할 수 있어야 하므로 허용합니다.
        );

        // 로그아웃 기능을 설정합니다.
        http.logout(logout -> logout
                .logoutUrl("/user/logout") // 로그아웃을 처리할 URL
                .logoutSuccessUrl("/user/loginForm") // 로그아웃 성공 시 이동할 페이지
                .invalidateHttpSession(true) // 로그아웃 시 세션을 무효화합니다.
        );

        return http.build();
    }
}