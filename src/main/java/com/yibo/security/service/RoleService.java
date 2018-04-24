package com.yibo.security.service;

import com.yibo.security.entity.Role;

import java.util.List;

public interface RoleService {
    List<Role> getRoleValuesByUserId(Long userId);
}