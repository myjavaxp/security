package com.yibo.security.service.impl;

import com.yibo.security.aop.LoggerManager;
import com.yibo.security.utils.SHA256Util;
import io.jsonwebtoken.lang.Assert;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

import static java.util.Collections.emptyList;

@Service("jwtAuthenticationProvider")
public class JWTAuthenticationProvider implements AuthenticationProvider {
    @Resource
    private UserDetailsServiceImpl userDetailsServiceImpl;

    @Override
    @LoggerManager(description = "登录密码验证")
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String name = authentication.getName();
        String password = authentication.getCredentials().toString();
        UserDetails userDetails = userDetailsServiceImpl.loadUserByUsername(name);
        if (null != userDetails) {
            String encodePassword = SHA256Util.getSHA256(password);//这里做密码解码
            Assert.isTrue(userDetails.getPassword().equals(encodePassword), "密码错误");
            return new UsernamePasswordAuthenticationToken(name, password, emptyList());
        } else {
            throw new UsernameNotFoundException("用户不存在");
        }
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }
}
