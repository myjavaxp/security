package com.yibo.security.service.impl;

import com.yibo.security.dao.RoleResourceDao;
import com.yibo.security.entity.Resource;
import com.yibo.security.service.ResourceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service("resourceService")
public class ResourceServiceImpl implements ResourceService {
    private final RoleResourceDao roleResourceDao;

    @Autowired
    public ResourceServiceImpl(RoleResourceDao roleResourceDao) {
        this.roleResourceDao = roleResourceDao;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Resource> getPermissionsByRoleId(Long roleId) {
        return roleResourceDao.getPermissionsByRoleId(roleId);
    }
}