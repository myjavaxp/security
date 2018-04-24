package com.yibo.security.dao;

import com.yibo.security.entity.Role;
import com.yibo.security.entity.UserRole;

import java.util.List;

public interface UserRoleDao {
    int deleteByPrimaryKey(Long id);

    int insert(UserRole record);

    int insertSelective(UserRole record);

    UserRole selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(UserRole record);

    int updateByPrimaryKey(UserRole record);

    List<Role> getRoleValuesByUserId(Long userId);
}