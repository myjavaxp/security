package com.yibo.security.service.impl;

import com.yibo.security.dao.UserRoleDao;
import com.yibo.security.entity.Role;
import com.yibo.security.service.RoleService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

@Service("roleService")
public class RoleServiceImpl implements RoleService {
    @Resource
    private UserRoleDao userRoleDao;

    @Override
    @Transactional(readOnly = true)
    public List<Role> getRoleValuesByUserId(Long userId) {
        return userRoleDao.getRoleValuesByUserId(userId);
    }
}