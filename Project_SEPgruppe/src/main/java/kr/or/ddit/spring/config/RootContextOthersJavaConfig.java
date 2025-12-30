package kr.or.ddit.spring.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.PropertiesFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;

@Configuration
public class RootContextOthersJavaConfig {
	@Bean
	public PropertiesFactoryBean fileInfo(
		@Value("classpath:kr/or/ddit/FileInfo.properties") Resource location	
	) {
		PropertiesFactoryBean factory = new PropertiesFactoryBean();
		factory.setLocation(location);
		return factory;
	}
	
	@Bean
	public MultipartResolver filterMultipartResolver(
		@Value("#{fileInfo.maxFileSize}") long maxFileSize
		, @Value("#{fileInfo.maxRequestSize}") long maxRequestSize
	) {
		CommonsMultipartResolver multipartResolver = new CommonsMultipartResolver();
		multipartResolver.setMaxUploadSize(maxRequestSize*1024*1024);
		multipartResolver.setMaxUploadSizePerFile(maxFileSize*1024*1024);
		return multipartResolver;
	}
}















