package kr.or.ddit.spring.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import kr.or.ddit.common.CompanyInterceptor;
import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

    private final CompanyInterceptor companyInterceptor;

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		// 'CompanyInterceptor'를 모든 URL에 대해 적용
		registry.addInterceptor(companyInterceptor)
        		.addPathPatterns("/**")  // 모든 요청에 Interceptor 적용
				.excludePathPatterns("/login", "/login/process");  // 로그인 관련 요청은 제외
	}
	
}
