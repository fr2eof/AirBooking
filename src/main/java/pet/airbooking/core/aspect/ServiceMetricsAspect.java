package pet.airbooking.core.aspect;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
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
public class ServiceMetricsAspect {

    private final MeterRegistry registry;

    @Around("execution(* pet.airbooking..service..*(..))")
    public Object measureServiceMethods(ProceedingJoinPoint pjp) throws Throwable {

        String method = pjp.getSignature().toShortString();

        Timer.Sample sample = Timer.start(registry);

        try {
            return pjp.proceed();
        } finally {
            sample.stop(
                    Timer.builder("service_method_latency")
                            .description("Service layer execution time")
                            .tag("method", method)
                            .register(registry)
            );
        }
    }
}
