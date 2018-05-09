package com.yibo.security.aop;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
@Aspect
public class LoggerAdvice {
    private static final Logger LOGGER = LoggerFactory.getLogger(LoggerAdvice.class);

    @Before("within(com.yibo.security..*) && @annotation(loggerManager)")
    public void addBeforeLogger(JoinPoint joinPoint, LoggerManager loggerManager) {
        LOGGER.info("执行[{}]开始", loggerManager.description());
        LOGGER.info("获取方法签名[{}]", joinPoint.getSignature().toString());
        LOGGER.info("传入参数:[{}]", parseParams(joinPoint.getArgs()));
    }

    @AfterReturning("within(com.yibo.security..*) && @annotation(loggerManager)")
    public void addAfterReturningLogger(LoggerManager loggerManager) {
        LOGGER.info("执行[{}]结束", loggerManager.description());
    }

    @AfterThrowing(pointcut = "within(com.yibo.security..*) && @annotation(loggerManager)", throwing = "ex")
    public void addAfterThrowingLogger(LoggerManager loggerManager, Exception ex) {
        LOGGER.error("执行[{}]发生异常", loggerManager.description(), ex);
    }

    private String parseParams(Object[] params) {
        if (null == params || params.length <= 0) {
            return "";
        }
        StringBuilder param = new StringBuilder();
        for (Object obj : params) {
            param.append(ToStringBuilder.reflectionToString(obj)).append("  ");
        }
        return param.toString();
    }
}