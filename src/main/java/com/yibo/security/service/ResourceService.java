package com.yibo.security.service;

import com.yibo.security.entity.Resource;

import java.util.List;

public interface ResourceService {
    List<Resource> getPermissionsByRoleId(Long roleId);
}
