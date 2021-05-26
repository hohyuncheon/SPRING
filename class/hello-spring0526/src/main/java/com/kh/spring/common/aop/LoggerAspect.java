package com.kh.spring.common.aop;


import java.math.BigDecimal;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Aspect //aspect클래스 등록 xml말고 다른버젼임. pointcut, advice를 가지고있음. 이거는 annotation버젼
@Component //빈등록을 위한용도
public class LoggerAspect {
	
	//aspect용
	@Pointcut("execution(* com.kh.spring.memo..selectMemoList(..))")
	public void loggerPointcut() {}
	
	//aspect용 stopwatch
	@Pointcut("execution(* com.kh.spring.memo.controller.MemoController.insertMemo(..))")
	public void loggerPointcut2() {}
	/**
	 * Around Advice
	 * - JoinPoint 실행전, 실행후에 삽입되어 처리되는 advice(보조업무)
	 * 
	 * @param joinPoint
	 * @return
	 * @throws Throwable 
	 */
	
	//aspect용
	//around () 속을 위에 loggerPoiontcut 메소드 이름으로 연결해줘야함
	//xml일때는 around 빼도된다.
	
	@Around("loggerPointcut()")
	public Object logger(ProceedingJoinPoint joinPoint) throws Throwable {
		Signature signature = joinPoint.getSignature();
		
		//before
		log.debug("----- {} start -----", signature);
		
		//주업무 joinPoint실행
		Object returnObj = joinPoint.proceed();
		
		//after
		log.debug("----- {} end -----", signature);

		return returnObj;
	}
	
	
	
	@Around("loggerPointcut2()")
	public Object logger2(ProceedingJoinPoint joinPoint) throws Throwable {
		Signature signature = joinPoint.getSignature();
		
		//before
		log.debug("----- {} start -----", signature);
		//stop watch
		StopWatch stopWatch = new StopWatch();
        stopWatch.start("Long type");
        System.out.println("insert시작시간 " + stopWatch.shortSummary());

		
		//주업무 joinPoint실행
		Object returnObj = joinPoint.proceed();
		
		//after
		log.debug("----- {} end -----", signature);

		stopWatch.stop();
	    System.out.println("insert걸린시간 " + stopWatch.shortSummary());

		return returnObj;
	}
	
}
