package snmaddula.components.logger;

import static org.slf4j.LoggerFactory.getLogger;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.util.StopWatch;

/**
 * 
 * @author snmaddula
 *
 */
@Aspect
class LoggingAspect {

	@Pointcut("@within(org.springframework.web.bind.annotation.RestController)")
	private void inRestControllers() {}

	@Pointcut("@within(org.springframework.stereotype.Service)")
	private void inServices() {}
	
	@Pointcut("inRestControllers() || inServices()")
	private void aopAwareClasses() {}
	
	@Around("aopAwareClasses()")
	public Object coverMethodExecution(ProceedingJoinPoint joinPoint) throws Throwable {
		logEnteringMethod(joinPoint);
		
		final StopWatch watch = new StopWatch();
		watch.start();
		final Object returnVal = joinPoint.proceed();
		watch.stop();
		
		logLeavingMethod(joinPoint, returnVal, watch);
		
		return returnVal;
	}
	
	@AfterThrowing(pointcut = "aopAwareClasses()", throwing = "ex")
	public void coverExceptions(JoinPoint joinPoint, Exception ex) {
		logError(joinPoint, "Failed: "+ joinPoint.getSignature() + "[ " + paramsWithValues(joinPoint) + " ]", ex);
	}


	private void logEnteringMethod(JoinPoint joinPoint) {
		logInfo(joinPoint, "Started: " + joinPoint.getSignature() + "[ " + paramsWithValues(joinPoint) + " ]");
	}
	
	private void logLeavingMethod(JoinPoint joinPoint, Object returnVal, StopWatch watch) {
		logInfo(joinPoint, "Finished: " + joinPoint.getSignature() + "[" + paramsWithValues(joinPoint) + "]"
				+ ", returned: " + returnVal + " in " + watch.getTotalTimeMillis() + " ms");
	}
	

	private String[] getParameterNames(JoinPoint joinPoint) {
		return ((MethodSignature) joinPoint.getSignature()).getParameterNames();
	}
	
	private void logError(JoinPoint joinPoint, String message, Exception ex) {
		getLogger(joinPoint.getTarget().getClass().getName()).error(message, ex);
	}
	
	private void logInfo(JoinPoint joinPoint, String message) {
		getLogger(joinPoint.getTarget().getClass().getName()).info(message);
	}
	
	private String paramsWithValues(JoinPoint joinPoint) {
		StringBuilder sb = new StringBuilder();
		String[] params = getParameterNames(joinPoint);
		Object[] values = joinPoint.getArgs();
		if(params != null && params.length > 0) {
			for(int i=0; i< params.length; i++) {
				sb.append(params[i]).append("=").append(values[i]).append(",");
			}
			sb.deleteCharAt(sb.length() - 1);
		}
		return sb.toString();
	}
}

