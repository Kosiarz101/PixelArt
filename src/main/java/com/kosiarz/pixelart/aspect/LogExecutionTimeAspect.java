package com.kosiarz.pixelart.aspect;

import com.kosiarz.pixelart.annotation.LogExecutionTime;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Aspect
@Component
public class LogExecutionTimeAspect {

    private static final Logger log = LoggerFactory.getLogger(LogExecutionTimeAspect.class);

    @Around("@annotation(logExecutionTime)")
    public Object logExecutionTime(ProceedingJoinPoint joinPoint, LogExecutionTime logExecutionTime) throws Throwable {
        Instant start = Instant.now();

        try {
            return joinPoint.proceed();
        } finally {
            Instant end = Instant.now();

            String taskName = logExecutionTime.value().isEmpty()
                    ? joinPoint.getSignature().toShortString()
                    : logExecutionTime.value();

            long durationInMilliseconds = start.until(end).toMillis();
            double durationInSeconds = durationInMilliseconds / 1000.0;
            log.info("{} executed in {} s", taskName, durationInSeconds);
        }
    }
}
