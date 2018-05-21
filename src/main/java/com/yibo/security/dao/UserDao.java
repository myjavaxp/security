package com.yibo.security.dao;

import com.yibo.security.entity.SysUser;
import com.yibo.security.entity.UserEntity;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface UserDao {
    int deleteByPrimaryKey(Long id);

    int insert(UserEntity record);

    int insertSelective(UserEntity record);

    UserEntity selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(UserEntity record);

    int updateByPrimaryKey(UserEntity record);

    UserEntity findByUsername(String username);

    SysUser findUserDetailsByUserId(Long userId);

    int insertUsers(@Param("users") List<UserEntity> userEntityList);
}