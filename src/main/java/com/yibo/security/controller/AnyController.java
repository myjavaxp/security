package com.yibo.security.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/any")
public class AnyController {
    @GetMapping
    public String getAny(){
        return "这个页面谁都可以访问";
    }
}
