package kr.or.ddit.spring.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import kr.or.ddit.security.CustomAuthenticationSuccessHandler;

// Spring Security FilterChain 사용 설정
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final CustomAuthenticationSuccessHandler successHandler;

    // 로그인 성공 시 실행될 커스텀 성공 핸들러
    public SecurityConfig(CustomAuthenticationSuccessHandler successHandler) {
        this.successHandler = successHandler;
    }

    // Spring Security가 UserDetailsService, PasswordEncoder를 사용해서 
    // 아이디 조회, 비밀번호 비교, 인증 성공 실패 판단 
    // 로그인 자체를 실제로 수행하는 엔진
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    // 비밀번호 암호화, 비교 담당
    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    // Spring Security FilterChain
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        // URL 접근 권한 설정
        http
            .authorizeHttpRequests(auth -> auth

                // 그룹웨어(직원/회사) 전용
                .requestMatchers(
                    new AntPathRequestMatcher("/groupware"),
                    new AntPathRequestMatcher("/groupware/**")
                ).hasAnyAuthority("EMPLOYEE", "COMPANY")

                // PROVIDER 전용 허용 URL
                .requestMatchers(new AntPathRequestMatcher("/provider/**")).hasAuthority("PROVIDER")
                .requestMatchers(new AntPathRequestMatcher("/subscriptionPlan/manage/**")).hasAuthority("PROVIDER")
                .requestMatchers(new AntPathRequestMatcher("/company")).hasAuthority("PROVIDER")
                .requestMatchers(new AntPathRequestMatcher("/payment")).hasAuthority("PROVIDER")

                // COMPANY 전용 허용 URL
                .requestMatchers(new AntPathRequestMatcher("/company/mypage/**")).hasAuthority("COMPANY")
                .requestMatchers(new AntPathRequestMatcher("/company/edit")).hasAuthority("COMPANY")

                // COMPANY 결제 진행 허용 URL
                .requestMatchers(new AntPathRequestMatcher("/payment/subPayment")).hasAuthority("COMPANY")
                .requestMatchers(new AntPathRequestMatcher("/payment/saveBillingKey")).hasAuthority("COMPANY")
                .requestMatchers(new AntPathRequestMatcher("/payment/schedule")).hasAuthority("COMPANY")

                // 위에 걸리지 않는 요청은 전부 허용
                .anyRequest().permitAll()
            )

            // 로그인 설정
            // /login/loginProcess로 POST 되면 Security가 가로챔
            // CustomUserDetailService 호출
            // 비밀번호 검증
            // 성공 시 CustomAuthenticationSuccessHandler 실행
            .formLogin(login -> login

                .loginPage("/login")                        // 로그인 화면
                .loginProcessingUrl("/login/loginProcess")  // 실제 인증 처리 URL
                .usernameParameter("userId")                // form input name
                .passwordParameter("userPw")                // form input name
                .successHandler(successHandler)             // 로그인 성공 후 처리
                .permitAll()
            )

            // 로그아웃 설정
            // /login/logout로 POST 되면 로그아웃
            // 성공 시 /login으로 이동
            // GET 로그아웃 금지
            .logout(logout -> logout
                .logoutRequestMatcher(new AntPathRequestMatcher("/login/logout", "POST"))
                .logoutSuccessUrl("/login")
            );

        return http.build();
    }
}
