package com.yibo.security.aop;

import com.alibaba.fastjson.JSON;
import com.yibo.security.constants.CommonConstants;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
@Aspect
public class LoggerAdvice {
    private static final Logger LOGGER = LoggerFactory.getLogger(LoggerAdvice.class);
    private static final String EMPTY = "";

    @Pointcut("within(com.yibo.security..*)")
    public void logPointcut() {

    }

    @Before("logPointcut()&&@annotation(loggerManager)")
    public void addBeforeLogger(JoinPoint joinPoint, LoggerManager loggerManager) {
        LOGGER.info("执行[{}]开始", loggerManager.description());
        LOGGER.info("方法名为:[{}]", joinPoint.getSignature().toShortString());
        LOGGER.info("传入参数为:\n{}", parseParams(joinPoint.getArgs()));
    }

    @AfterReturning(pointcut = "logPointcut()&&@annotation(loggerManager)", returning = "result")
    public void addAfterReturningLogger(LoggerManager loggerManager, Object result) {
        LOGGER.info("执行[{}]结束", loggerManager.description());
        LOGGER.info("执行结果为:\n{}", JSON.toJSONString(result, CommonConstants.FEATURES));
    }

    @AfterThrowing(pointcut = "logPointcut()&&@annotation(loggerManager)", throwing = "e")
    public void addAfterThrowingLogger(LoggerManager loggerManager, Exception e) {
        LOGGER.error("执行[{}]发生异常:{}", loggerManager.description(), e.getMessage());
    }

    private String parseParams(Object[] params) {
        if (null == params) {
            return EMPTY;
        }
        int size = params.length;
        if (size < 1) {
            return EMPTY;
        }
        StringBuilder param = new StringBuilder();
        for (int i = 0; i < size - 1; i++) {
            param.append(JSON.toJSONString(params[i], CommonConstants.FEATURES)).append(",\n");
        }
        param.append(JSON.toJSONString(params[size - 1], CommonConstants.FEATURES));
        return param.toString();
    }
}