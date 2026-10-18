package org.example.SpringBoot.AuthREST;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Slf4j
@Aspect
@Component
public class ExecutionLoggingAspect {

    @Pointcut("within(com.example.order..*Controller) || within(com.example.order..*Service)")
    public void applicationPackagePointcut() {}

    @Around("applicationPackagePointcut()")
    public Object logExecutionTimeAndAudit(ProceedingJoinPoint joinPoint) throws Throwable {
        String methodName = joinPoint.getSignature().toShortString();
        Object[] args = joinPoint.getArgs();

        log.info("Enter: {} with argument[s] = {}", methodName, Arrays.toString(args));
        long start = System.currentTimeMillis();

        try {
            Object result = joinPoint.proceed();
            long duration = System.currentTimeMillis() - start;
            log.info("Exit: {} with result = {}; execution time = {} ms", methodName, result, duration);
            return result;
        } catch (IllegalArgumentException ex) {
            log.error("Illegal argument in {}: {}", methodName, Arrays.toString(args), ex);
            throw ex;
        } catch (Throwable ex) {
            long duration = System.currentTimeMillis() - start;
            log.warn("Exception in {} after {} ms: {}", methodName, duration, ex.getMessage());
            throw ex;
        }
    }
}
