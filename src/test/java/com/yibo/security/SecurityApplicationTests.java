package com.yibo.security;

import com.yibo.security.entity.UserAuthorization;
import com.yibo.security.service.UserService;
import com.yibo.security.utils.JSONUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

@RunWith(SpringRunner.class)
@SpringBootTest
public class SecurityApplicationTests {
    @Resource
    private UserService userService;

    @Test
    public void contextLoads() {
        UserAuthorization userAuthorization = userService.getUserAuthorization("admin");
        System.out.println(JSONUtil.toJson(userAuthorization));
    }
}