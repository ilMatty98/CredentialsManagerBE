package com.credentialsmanager.controller;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Slf4j
@Aspect
@Component
public class ControllerLoggingAspect {

    @Before("execution(* com.credentialsmanager.controller.*.*(..))")
    public void logMethodStart(JoinPoint joinPoint) {
        log.info("Start method {}", getMethodName(joinPoint));
    }

    @After("execution(* com.credentialsmanager.controller.*.*(..))")
    public void logMethodEnd(JoinPoint joinPoint) {
        log.info("End method {}", getMethodName(joinPoint));
    }

    private String getMethodName(JoinPoint joinPoint) {
        return Optional.ofNullable(joinPoint)
                .map(JoinPoint::getSignature)
                .map(Signature::toShortString)
                .orElse("");
    }
}
