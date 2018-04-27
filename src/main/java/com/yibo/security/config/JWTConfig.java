package com.yibo.security.config;

import com.yibo.security.filter.JWTAuthenticationFilter;
import com.yibo.security.filter.JWTLoginFilter;
import com.yibo.security.service.ResourceService;
import com.yibo.security.service.RoleService;
import com.yibo.security.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import redis.clients.jedis.JedisPool;

@Configuration
public class JWTConfig {
    @Autowired
    public void setResourceService(ResourceService resourceService) {
        JWTAuthenticationFilter.setResourceService(resourceService);
    }

    @Autowired
    public void setRoleService(RoleService roleService) {
        JWTAuthenticationFilter.setRoleService(roleService);
    }

    @Autowired
    public void setUserService(UserService userService) {
        JWTAuthenticationFilter.setUserService(userService);
    }

    @Autowired
    public void setJWTAuthenticationFilterJedisPool(JedisPool jedisPool) {
        JWTAuthenticationFilter.setJedisPool(jedisPool);
    }

    @Autowired
    public void setJedisPool(JedisPool jedisPool) {
        JWTLoginFilter.setJedisPool(jedisPool);
    }
}