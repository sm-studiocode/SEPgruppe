package kr.or.ddit.spring.config;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Properties;

import javax.sql.DataSource;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;
import org.apache.commons.dbcp2.BasicDataSource;
import org.apache.ibatis.annotations.Mapper;
import org.aspectj.lang.annotation.Aspect;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.mapper.MapperScannerConfigurer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.PropertiesFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.TransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;

@Configuration
@PropertySource("classpath:application.properties")
@ComponentScan(basePackages = "kr.or.ddit"
	, excludeFilters = {
		@ComponentScan.Filter(classes = Controller.class)	
	}
	, includeFilters = {
		@ComponentScan.Filter(classes = Aspect.class)
	}
)
@EnableAspectJAutoProxy
@EnableTransactionManagement
public class RootContextJavaConfig {
	
	// properties 값 주입 가능하게 하는 Bean
	@Bean
	public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
	    return new PropertySourcesPlaceholderConfigurer();
	}
	
	// DBInfo.properties 파일 로딩
	@Bean
	public PropertiesFactoryBean dbInfo(
		@Value("classpath:kr/or/ddit/db/DBInfo.properties") Resource location	
	) {
		PropertiesFactoryBean factory = new PropertiesFactoryBean();
		factory.setLocation(location);
		return factory;
	}
	
	// DB 커넥션 풀
	@Bean
	public DataSource dataSource(
		@Value("#{dbInfo.driverClassName}") String driverClassName
		, @Value("#{dbInfo['url']}") String url
		, @Value("#{dbInfo.user}") String user
		, @Value("#{dbInfo.password}") String password
		, @Value("#{dbInfo.initialSize}") int initialSize
		, @Value("#{dbInfo.maxWait}") long maxWait
		, @Value("#{dbInfo.maxTotal}") int maxTotal
	) {
		BasicDataSource ds = new BasicDataSource();
		ds.setDriverClassName(driverClassName);
		ds.setUrl(url);
		ds.setUsername(user);
		ds.setPassword(password);
		ds.setInitialSize(initialSize);
		ds.setMaxWait(Duration.of(maxWait/1000, ChronoUnit.SECONDS));
		ds.setMaxTotal(maxTotal);
		return ds;
	}
	
	/**
	 * FactoryBean 의 특성
	 * FactoryBean 을 bean 으로 등록한 경우,
	 * 실제 bean 으로 등록되는 객체는 getObject 메소드의 반환 객체.
	 * @param dataSource
	 * @param configLocation
	 * @return
	 */
	@Bean
	public SqlSessionFactoryBean sqlSessionFactory(
		DataSource dataSource	
		, @Value("classpath:kr/or/ddit/works/mybatis/Configuration.xml") Resource configLocation
		, @Value("classpath:kr/or/ddit/works/mybatis/mappers/*.xml") Resource...mapperLocations
	) {
		SqlSessionFactoryBean factoryBean = new SqlSessionFactoryBean();
		factoryBean.setDataSource(dataSource);
		factoryBean.setConfigLocation(configLocation);
		factoryBean.setMapperLocations(mapperLocations);
		factoryBean.setTypeAliasesPackage("kr.or.ddit.works.**.vo");
		return factoryBean;
	}
	
	// MyBatis Mapper 인터페이스 자동 등록
	@Bean
	public MapperScannerConfigurer mapperScanner() {
		MapperScannerConfigurer configurar = new MapperScannerConfigurer();
		configurar.setBasePackage("kr.or.ddit.works.mybatis.mappers");
		configurar.setAnnotationClass(Mapper.class);
		configurar.setSqlSessionFactoryBeanName("sqlSessionFactory");
		return configurar;
	}
	
	// 트랜잭션 매니저 등록
	@Bean
	public TransactionManager transactionManager(
			DataSource dataSource
	) {
		return new DataSourceTransactionManager(dataSource);
	}
	
	// 메일 발송 설정
	@Bean
	public JavaMailSender mailSender(
	        @Value("${mail.host}") String host,
	        @Value("${mail.port}") int port,
	        @Value("${mail.username}") String username,
	        @Value("${mail.password}") String password,
	        @Value("${mail.smtp.auth:true}") boolean auth,
	        @Value("${mail.smtp.starttls.enable:true}") boolean starttlsEnable,
	        @Value("${mail.smtp.ssl.trust:smtp.gmail.com}") String trust
	) {
	    JavaMailSenderImpl sender = new JavaMailSenderImpl();
	    sender.setHost(host);
	    sender.setPort(port);
	    sender.setUsername(username);
	    sender.setPassword(password);
	    sender.setDefaultEncoding("UTF-8");

	    Properties props = sender.getJavaMailProperties();
	    props.put("mail.transport.protocol", "smtp");
	    props.put("mail.smtp.auth", String.valueOf(auth));
	    props.put("mail.smtp.starttls.enable", String.valueOf(starttlsEnable));
	    props.put("mail.smtp.starttls.required", "true");
	    props.put("mail.smtp.ssl.trust", trust);
	    props.put("mail.debug", "true");

	    return sender;
	}
	
	// 파일 업로드 설정
	@Bean
	public MultipartResolver multipartResolver(
	        @Value("${file.maxFileSize}") long maxFileSize,
	        @Value("${file.maxRequestSize}") long maxRequestSize
	) {
	    CommonsMultipartResolver multipartResolver = new CommonsMultipartResolver();
	    multipartResolver.setMaxUploadSize(maxRequestSize * 1024 * 1024);
	    multipartResolver.setMaxUploadSizePerFile(maxFileSize * 1024 * 1024);
	    return multipartResolver;
	}

}











