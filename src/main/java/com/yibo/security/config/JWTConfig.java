package com.yibo.security.config;

import com.yibo.security.filter.JWTAuthenticationFilter;
import com.yibo.security.filter.JWTLoginFilter;
import com.yibo.security.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import redis.clients.jedis.JedisPool;

@Configuration
public class JWTConfig {
    @Autowired
    public void setJWTAuthenticationFilterJedisPool(JedisPool jedisPool) {
        JWTAuthenticationFilter.setJedisPool(jedisPool);
    }

    @Autowired
    public void setJedisPool(JedisPool jedisPool) {
        JWTLoginFilter.setJedisPool(jedisPool);
    }

    @Autowired
    public void setUserService(UserService userService) {
        JWTLoginFilter.setUserService(userService);
    }
}