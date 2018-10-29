package com.yibo.security.config;

import com.yibo.security.filter.JWTAuthenticationFilter;
import com.yibo.security.filter.JWTLoginFilter;
import com.yibo.security.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.StringRedisTemplate;

@Configuration
public class JWTConfig {
    @Autowired
    public void setJWTAuthenticationFilterRedis(StringRedisTemplate stringRedisTemplate) {
        JWTAuthenticationFilter.setRedisTemplate(stringRedisTemplate);
    }

    @Autowired
    public void setRedisTemplate(StringRedisTemplate stringRedisTemplate) {
        JWTLoginFilter.setRedisTemplate(stringRedisTemplate);
    }

    @Autowired
    public void setUserService(UserService userService) {
        JWTLoginFilter.setUserService(userService);
    }
}