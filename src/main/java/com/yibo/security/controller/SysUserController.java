package com.yibo.security.controller;

import com.yibo.security.entity.UserEntity;
import com.yibo.security.service.UserService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("/users")
public class SysUserController {
    @Resource
    private UserService userService;

    @PostMapping
    public String addUser(@RequestBody UserEntity userEntity) {
        userService.insertUser(userEntity);
        return "新增用户成功";
    }
}