package com.yibo.security.dao;

import com.yibo.security.entity.Resource;
import com.yibo.security.entity.Role;
import com.yibo.security.entity.UserRole;

import java.util.List;
import java.util.Set;

public interface UserRoleDao {
    int deleteByPrimaryKey(Long id);

    int insert(UserRole record);

    int insertSelective(UserRole record);

    UserRole selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(UserRole record);

    int updateByPrimaryKey(UserRole record);

    List<Role> getRoleValuesByUserId(Long userId);

    Set<Resource> getResourcesByUserId(Long userId);
}