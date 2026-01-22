package kr.or.ddit.spring.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import kr.or.ddit.common.CompanyInterceptor;
import lombok.RequiredArgsConstructor;

// Interceptor 설정 클래스

// Spring MVC Interceptor 등록 설정
// CompanyInterceptor를 모든 요청에 적용
// - 로그인, 리소스는 제외
@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

    private final CompanyInterceptor companyInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(companyInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns(
                        "/login/**",
                        "/resources/**"
                );
    }
}
