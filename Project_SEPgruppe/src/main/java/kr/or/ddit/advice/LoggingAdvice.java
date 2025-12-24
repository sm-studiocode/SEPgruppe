package kr.or.ddit.advice;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

import lombok.extern.slf4j.Slf4j;

/**
 * 모든 비즈니스 로직을 대상으로 실행시점에
 * 시스템 로그로 상황, 해당 로직의 실행 소요 시간을 기록하라.
 * 1. core concern : 모든 비즈니스 로직 --> Target 
 * (kr.or.ddit.member.service.***)
 * 2. cross cutting concern : 시스템 로깅 --> Advice + Pointcut =>Aspect
 * weaver 에 의해 advice 와 target 이 weaving 됨.
 * 
 *   위빙 시점에 따른 advice 종류
 *   1. before advice : target 메소드 실행 전에 위빙
 *   2. after advice : target 메소드 실행 이후에 위빙
 *   3. after-returing advice : target 메소드가 정상 실행 종료된 경우 위빙
 *   4. after-throwing advice : target 메소드에서 예외가 throw 된 경우 위빙
 *   5. around weaving advice : target 메소드를 직접 실행하는 advice
 *   
 */
@Slf4j
@Aspect
public class LoggingAdvice {
	
	@Pointcut("execution(* kr.or.ddit..service.*.*(..))")
	public void dummy() {}
	
//	@Before("dummy()")
	public void before() {
		log.info("===============target 에 [before] weaving 됨.==============");
		
	}
	
//	@After("dummy()")
	public void after() {
		log.info("----------------target 에 [after] weaving 됨.--------------");
		
	}
	
	@Around("dummy()")
	public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
		long start = System.currentTimeMillis();
		
		Object target = joinPoint.getTarget();
		Signature signature = joinPoint.getSignature();
		
		log.info("{}.{} 실행(before)", target.getClass().getSimpleName(), signature.getName());
		Object[] args = joinPoint.getArgs();
		Object returnValue;
		try {
			returnValue = joinPoint.proceed(args);
			long end = System.currentTimeMillis();
			log.info("{}.{} 실행(after), 소요시간 : {}ms" 
					, target.getClass().getSimpleName(), signature.getName()
					, end-start );
			
			return returnValue;
		} catch (Throwable e) {
			throw e;
		}
		
	}
}

















