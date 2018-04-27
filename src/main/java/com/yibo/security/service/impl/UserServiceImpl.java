package com.yibo.security.service.impl;

import com.yibo.security.dao.UserDao;
import com.yibo.security.entity.UserEntity;
import com.yibo.security.service.UserService;
import com.yibo.security.utils.SHA256Util;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

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

    @Override
    @Transactional
    public void insertUser(UserEntity userEntity) {
        userEntity.setId(null);
        Assert.hasText(userEntity.getUsername(), "用户名不能为空！");
        Assert.hasText(userEntity.getPassword(), "密码不能为空！");
        userEntity.setPassword(SHA256Util.getSHA256(userEntity.getPassword()));
        userDao.insertSelective(userEntity);
    }
}