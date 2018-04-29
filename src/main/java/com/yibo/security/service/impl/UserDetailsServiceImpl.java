package com.yibo.security.service.impl;

import com.yibo.security.entity.UserEntity;
import com.yibo.security.service.UserService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

import static java.util.Collections.emptyList;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    @Resource
    private UserService userService;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserEntity userEntity = userService.findUserByUsername(username);
        if (null == userEntity) {
            throw new UsernameNotFoundException("用户:" + username + ",不存在!");
        }
        return new User(
                userEntity.getUsername(),
                userEntity.getPassword(),
                true,//这四个哥们可以做成动态赋值
                true,
                true,
                true,
                emptyList());
    }
}