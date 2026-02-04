package kr.or.ddit.spring.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import kr.or.ddit.common.CompanyInterceptor;
import kr.or.ddit.security.SubscriptionGuardInterceptor;
import lombok.RequiredArgsConstructor;

// Interceptor 설정 클래스

// Spring MVC Interceptor 등록 설정
// CompanyInterceptor를 모든 요청에 적용
// - 로그인, 리소스는 제외
@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

    private final CompanyInterceptor companyInterceptor;

    // 그룹웨어 구독 체크 인터셉터
    private final SubscriptionGuardInterceptor subscriptionGuardInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(companyInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns(
                        "/login/**",
                        "/resources/**"
                );

        // COMPANY가 /groupware 들어갈 때 구독 여부를 검사
        // - 구독 없으면 /subscriptionPlan로 redirect
        // - 플랜/결제/로그인/리소스는 제외(무한 리다이렉트 방지)
        registry.addInterceptor(subscriptionGuardInterceptor)
                .addPathPatterns("/groupware", "/groupware/**")
                .excludePathPatterns(
                        "/subscriptionPlan/**",
                        "/payment/**",
                        "/login/**",
                        "/resources/**"
                );
    }
}
