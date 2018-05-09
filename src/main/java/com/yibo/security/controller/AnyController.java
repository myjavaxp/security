package com.yibo.security.controller;

import com.yibo.security.aop.LoggerManager;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/any")
public class AnyController {
    @GetMapping
    @LoggerManager(description = "访问公共接口")
    public String getAny(){
        return "Hello Any";
    }
}