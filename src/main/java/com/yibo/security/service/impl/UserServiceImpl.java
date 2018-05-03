package com.yibo.security.service.impl;

import com.yibo.security.dao.RoleResourceDao;
import com.yibo.security.dao.UserDao;
import com.yibo.security.entity.Resource;
import com.yibo.security.entity.Role;
import com.yibo.security.entity.UserAuthorization;
import com.yibo.security.entity.UserEntity;
import com.yibo.security.service.RoleService;
import com.yibo.security.service.UserService;
import com.yibo.security.utils.SHA256Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service("userService")
public class UserServiceImpl implements UserService {
    private final UserDao userDao;
    private final RoleService roleService;
    private final RoleResourceDao roleResourceDao;

    @Autowired
    public UserServiceImpl(UserDao userDao, RoleService roleService, RoleResourceDao roleResourceDao) {
        this.userDao = userDao;
        this.roleService = roleService;
        this.roleResourceDao = roleResourceDao;
    }

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

    @Override
    @Transactional(readOnly = true)
    public UserAuthorization getUserAuthorization(String username) {
        UserAuthorization userAuthorization = new UserAuthorization();
        UserEntity userEntity = userDao.findByUsername(username);
        List<Role> roleList = roleService.getRoleValuesByUserId(userEntity.getId());
        List<Resource> resourceList = roleResourceDao.getResourcesByRoles(roleList);
        Set<String> resources = new HashSet<>();
        Set<String> roles = new HashSet<>();
        roleList.forEach(role -> roles.add(role.getName()));
        resourceList.forEach(resource -> resources.add(resource.getUrl()));
        userAuthorization.setUsername(username);
        userAuthorization.setRoleList(roles);
        userAuthorization.setResourceList(resources);
        return userAuthorization;
    }
}