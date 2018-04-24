package com.yibo.security.service.impl;

import com.yibo.security.dao.UserDao;
import com.yibo.security.entity.UserEntity;
import com.yibo.security.service.UserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

@Service("userService")
public class UserServiceImpl implements UserService {
    @Resource
    private UserDao userDao;

    @Override
    @Transactional(readOnly = true)
    public UserEntity findUserByUsername(String username) {
        return userDao.findByUsername(username);
    }
}