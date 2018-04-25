package com.yibo.security.config;

import com.yibo.security.filter.JWTAuthenticationFilter;
import com.yibo.security.service.ResourceService;
import com.yibo.security.service.RoleService;
import com.yibo.security.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

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
}