package kr.or.ddit.spring.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import kr.or.ddit.security.CustomAuthenticationSuccessHandler;
import kr.or.ddit.works.mail.service.MailService;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

	@Autowired
    private MailService mailService;
	
//	@Bean
//    public CustomAuthenticationSuccessHandler customAuthenticationSuccessHandler() {
//        CustomAuthenticationSuccessHandler handler = new CustomAuthenticationSuccessHandler();
//        handler.setMailService(mailService); // ✅ 여기서 꼭 주입
//        return handler;
//    }
	
	@Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }
	
	@Bean
	public PasswordEncoder passwordEncoder() {
		return PasswordEncoderFactories.createDelegatingPasswordEncoder();
	}

    /**
     * 인증(AuthenticationManager)과 인가(AuthorizationManager)를 지원하는 필터 체인 구조 형성.
     * @param http
     * @return
     * @throws Exception
     */
	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

	    http
	        // 1. CSRF 보호 비활성화
	        // 로그인 흐름 이해에 집중하기 위해 CSRF를 비활성화
	        .csrf(customizer -> customizer.disable())

	        // 2. 요청에 대한 접근 제어 설정
	        // 모든 요청을 허용하고, 실제 접근 제어는 그룹웨어 시스템에서 담당하도록 설계
	        .authorizeHttpRequests(authorize ->
	            authorize.anyRequest().permitAll()
	        )

	        // 3. Form 기반 로그인 설정
	        .formLogin(customizer ->
	            customizer
	                // 로그인 페이지 URL
	                // Spring Security 기본 로그인 화면이 아닌 개발자가 직접 만든 로그인 페이지 사용
	                .loginPage("/login")

	                // 로그인 처리 URL
	                // 해당 URL로 POST 요청이 들어오면 Controller를 거치지 않고 Spring Security가 인증 처리
	                .loginProcessingUrl("/login/loginProcess")

	                // 로그인 폼에서 사용자 아이디 파라미터명
	                .usernameParameter("userId")

	                // 로그인 폼에서 비밀번호 파라미터명
	                .passwordParameter("userPw")

	                // 로그인 성공 시 실행되는 커스텀 핸들러
	                // 사용자의 구독/승인 상태를 확인한 뒤 그룹웨어 페이지로 이동시키는 역할을 담당
	                //.successHandler(customAuthenticationSuccessHandler())

	                // 로그인 관련 URL은 인증 없이 접근 허용
	                .permitAll()
	        )

	        // 4. 로그아웃 설정
	        .logout(customizer ->
	            customizer
	                // 로그아웃 처리 URL
	                // 해당 URL 호출 시 세션 무효화 및 인증 정보 제거
	                .logoutUrl("/logout")

	                // 로그아웃 성공 후 이동할 페이지
	                .logoutSuccessUrl("/login")
	        );

	    return http.build();
	}


}










