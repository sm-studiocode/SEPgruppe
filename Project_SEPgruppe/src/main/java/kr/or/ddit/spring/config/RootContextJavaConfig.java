package kr.or.ddit.spring.config;

import java.time.Duration;
import java.time.temporal.ChronoUnit;

import javax.sql.DataSource;

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
import org.springframework.stereotype.Controller;
import org.springframework.transaction.TransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
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
	
	@Bean
	public PropertiesFactoryBean dbInfo(
		@Value("classpath:kr/or/ddit/db/DBInfo.properties") Resource location	
	) {
		PropertiesFactoryBean factory = new PropertiesFactoryBean();
		factory.setLocation(location);
		return factory;
	}
	
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
	
	@Bean
	public MapperScannerConfigurer mapperScanner() {
		MapperScannerConfigurer configurar = new MapperScannerConfigurer();
		configurar.setBasePackage("kr.or.ddit.works.mybatis.mappers");
		configurar.setAnnotationClass(Mapper.class);
		configurar.setSqlSessionFactoryBeanName("sqlSessionFactory");
		return configurar;
	}
	
	@Bean
	public TransactionManager transactionManager(
			DataSource dataSource
	) {
		return new DataSourceTransactionManager(dataSource);
	}
}











