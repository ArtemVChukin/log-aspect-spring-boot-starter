package ru.alfastrah.library.log.aspect.starter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;

import static java.lang.System.currentTimeMillis;
import static java.lang.System.nanoTime;
import static java.time.ZoneId.systemDefault;
import static java.time.format.DateTimeFormatter.ofPattern;

@Aspect
public class AspectLogging {
    private static final ObjectMapper mapper = new ObjectMapper()
            .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
            .configure(SerializationFeature.WRAP_ROOT_VALUE, true);
    private static final DateTimeFormatter ISO_LOCAL_DATE_TIME_SHORT = ofPattern("yyyy-MM-dd HH:mm:ss.SSS");

    @Around("@annotation(aspectLog)")
    public Object logExecutionTime(ProceedingJoinPoint joinPoint, AspectLog aspectLog) throws Throwable {
        Logger log = LoggerFactory.getLogger(joinPoint.getSourceLocation().getWithinType());
        Instant start = Instant.now();
        boolean isRemoveMDC = false;
        if (MDC.get("UID") == null) {
            MDC.put("UID", String.valueOf(nanoTime()));
            isRemoveMDC = true;
        }
        if (log.isInfoEnabled()) {
            log.info("IN details [{}] start [{}] arguments [{}]", joinPoint.getSignature().toShortString(),
                    start.atZone(systemDefault()).format(ISO_LOCAL_DATE_TIME_SHORT),
                    getStringArguments(joinPoint.getArgs(), aspectLog.useJsonMapper()));
        }
        try {
            Object proceed = joinPoint.proceed();
            if (log.isInfoEnabled()) {
                log.info("OUT details [{}] duration [{}ms] return [{}]", joinPoint.getSignature().toShortString(),
                        (currentTimeMillis() - start.toEpochMilli()), aspectLog.useJsonMapper() ? valueAsStringSafe(proceed) : proceed);
            }
            return proceed;
        } catch (Exception e) {
            log.error("ERR details [{}] duration [{}ms] exception [{}]", joinPoint.getSignature().toShortString(),
                    (currentTimeMillis() - start.toEpochMilli()), e.getMessage(), e);
            throw e;
        } finally {
            if (isRemoveMDC) {
                MDC.remove("UID");
            }
        }
    }

    private String getStringArguments(Object[] args, boolean useJsonMapper) {
        if (useJsonMapper) {
            StringBuilder result = new StringBuilder("[");
            for (Object arg : args) {
                result.append(valueAsStringSafe(arg)).append(",");
            }
            result.setCharAt(result.length() - 1, ']');
            return result.toString();
        } else {
            return Arrays.toString(args);
        }
    }

    private String valueAsStringSafe(Object object) {
        try {
            return mapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            return object.toString();
        }
    }
}
