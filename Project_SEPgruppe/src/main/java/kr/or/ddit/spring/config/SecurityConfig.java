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

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final CustomAuthenticationSuccessHandler successHandler;

    public SecurityConfig(CustomAuthenticationSuccessHandler successHandler) {
        this.successHandler = successHandler;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
            .authorizeHttpRequests(auth -> auth

                // =========================
                // (A) PROVIDER 전용
                // =========================
                .requestMatchers(new AntPathRequestMatcher("/provider/**")).hasAuthority("PROVIDER")
                .requestMatchers(new AntPathRequestMatcher("/subscriptionPlan/manage/**")).hasAuthority("PROVIDER")

                // 고객사 목록 "딱 /company" 운영자 전용
                .requestMatchers(new AntPathRequestMatcher("/company")).hasAuthority("PROVIDER")

                // ✅ 운영자 결제 관리 "딱 /payment" 만 PROVIDER
                .requestMatchers(new AntPathRequestMatcher("/payment")).hasAuthority("PROVIDER")

                // =========================
                // (B) COMPANY 전용 (마이페이지)
                // =========================
                .requestMatchers(new AntPathRequestMatcher("/company/mypage/**")).hasAuthority("COMPANY")
                .requestMatchers(new AntPathRequestMatcher("/company/edit")).hasAuthority("COMPANY")

                // =========================
                // (C) COMPANY 결제 진행 허용 (★핵심)
                // =========================
                .requestMatchers(new AntPathRequestMatcher("/payment/subPayment")).hasAuthority("COMPANY")
                .requestMatchers(new AntPathRequestMatcher("/payment/saveBillingKey")).hasAuthority("COMPANY")
                .requestMatchers(new AntPathRequestMatcher("/payment/schedule")).hasAuthority("COMPANY")

                // (선택) 만약 결제 관련 추가 URL이 있으면 COMPANY로 추가
//                .requestMatchers(new AntPathRequestMatcher("/payment/complete/**")).hasAuthority("COMPANY")
//                .requestMatchers(new AntPathRequestMatcher("/payment/callback/**")).hasAuthority("COMPANY")

                // ❌ (D) /payment/** PROVIDER 잠금 이거 삭제해라. 이게 있으면 COMPANY 결제는 계속 터짐.
                // .requestMatchers(new AntPathRequestMatcher("/payment/**")).hasAuthority("PROVIDER")

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
