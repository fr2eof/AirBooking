package pet.airbooking.core.aspect;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class ServiceLoggingAspect {

    @Around("execution(* pet.airbooking..service..*(..))")
    public Object logServiceMethods(ProceedingJoinPoint pjp) throws Throwable {

        String method = pjp.getSignature().toShortString();
        Object[] args = pjp.getArgs();

        long start = System.currentTimeMillis();

        log.info("ENTER {} args={}", method, args);

        try {
            Object result = pjp.proceed();

            long time = System.currentTimeMillis() - start;

            log.info("EXIT {} result={} timeMs={}",
                    method,
                    result,
                    time);

            return result;

        } catch (Throwable ex) {

            long time = System.currentTimeMillis() - start;

            log.error("ERROR {} timeMs={} error={}",
                    method,
                    time,
                    ex.getMessage(),
                    ex);

            throw ex;
        }
    }
}
