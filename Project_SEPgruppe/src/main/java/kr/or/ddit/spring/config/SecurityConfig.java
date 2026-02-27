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

        http
            .csrf(csrf -> csrf.ignoringRequestMatchers(
                    new AntPathRequestMatcher("/employee/admin/**"),
                    new AntPathRequestMatcher("/adminpage/**"),
                    new AntPathRequestMatcher("/adminpage")
                ))

            .authorizeHttpRequests(auth -> auth

                /* ✅ 공지 등록/수정/삭제 보호 */
                .requestMatchers(
                    new AntPathRequestMatcher("/notice/new"),
                    new AntPathRequestMatcher("/notice/*/editForm"),
                    new AntPathRequestMatcher("/notice/*/edit"),
                    new AntPathRequestMatcher("/notice/delete")
                ).hasAnyAuthority(
                    "ROLE_TENANT_ADMIN",
                    "ROLE_NOTICE_ADMIN",
                    "ROLE_NOTICE_DEPT_ADMIN",
                    "ROLE_ADMIN" /* transitional */
                )

                // 그룹웨어 기본 진입
                .requestMatchers(
                        new AntPathRequestMatcher("/groupware"),
                        new AntPathRequestMatcher("/groupware/**")
                ).hasAnyAuthority("EMPLOYEE", "ROLE_TENANT_ADMIN", "ROLE_ADMIN" /* transitional */)

                // 관리자 전용 페이지(테넌트 관리자)
                .requestMatchers(
                        new AntPathRequestMatcher("/adminpage"),
                        new AntPathRequestMatcher("/adminpage/**"),
                        new AntPathRequestMatcher("/employee/admin/**"),
                        new AntPathRequestMatcher("/employee/departments")
                ).hasAnyAuthority("ROLE_TENANT_ADMIN", "ROLE_ADMIN" /* transitional */)

                // PROVIDER 전용
                .requestMatchers(new AntPathRequestMatcher("/provider/**")).hasAuthority("PROVIDER")
                .requestMatchers(new AntPathRequestMatcher("/subscriptionPlan/manage/**")).hasAuthority("PROVIDER")

                // COMPANY 전용
                .requestMatchers(new AntPathRequestMatcher("/company/mypage/**"))
                    .hasAnyAuthority("COMPANY", "ROLE_TENANT_ADMIN", "ROLE_ADMIN" /* transitional */)
                .requestMatchers(new AntPathRequestMatcher("/company/edit"))
                    .hasAnyAuthority("COMPANY", "ROLE_TENANT_ADMIN", "ROLE_ADMIN" /* transitional */)

                // 결제
                .requestMatchers(new AntPathRequestMatcher("/payment/subPayment"))
                    .hasAnyAuthority("COMPANY", "ROLE_TENANT_ADMIN", "ROLE_ADMIN" /* transitional */)
                .requestMatchers(new AntPathRequestMatcher("/payment/saveBillingKey"))
                    .hasAnyAuthority("COMPANY", "ROLE_TENANT_ADMIN", "ROLE_ADMIN" /* transitional */)
                .requestMatchers(new AntPathRequestMatcher("/payment/schedule"))
                    .hasAnyAuthority("COMPANY", "ROLE_TENANT_ADMIN", "ROLE_ADMIN" /* transitional */)

                .anyRequest().permitAll()
            )

            .formLogin(login -> login
                .loginPage("/login")
                .loginProcessingUrl("/login/loginProcess")
                .usernameParameter("userId")
                .passwordParameter("userPw")
                .successHandler(successHandler)
                .permitAll()
            )

            .logout(logout -> logout
                .logoutRequestMatcher(new AntPathRequestMatcher("/login/logout", "POST"))
                .logoutSuccessUrl("/login")
            );

        return http.build();
    }
}