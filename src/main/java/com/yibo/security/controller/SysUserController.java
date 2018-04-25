package com.yibo.security.controller;

import com.yibo.security.entity.UserEntity;
import com.yibo.security.service.UserService;
import com.yibo.security.utils.SHA256Util;
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
        userEntity.setPassword(SHA256Util.getSHA256(userEntity.getPassword()));
        userService.insertUser(userEntity);
        return "新增用户成功";
    }
}