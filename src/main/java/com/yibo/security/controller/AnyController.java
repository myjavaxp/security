package com.yibo.security.controller;

import com.yibo.security.aop.LoggerManager;
import com.yibo.security.dao.UserDao;
import com.yibo.security.entity.UserEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("/any")
public class AnyController {
    @Resource
    private UserDao userDao;

    @GetMapping
    @LoggerManager(description = "访问公共接口")
    public String getAny() {
        return "Hello Any";
    }

    @GetMapping("/cache")
    public void cache() {
        UserEntity user1 = userDao.selectByPrimaryKey(1L);
        UserEntity user2 = userDao.selectByPrimaryKey(1L);
        System.out.println(user1 == user2);
    }
}