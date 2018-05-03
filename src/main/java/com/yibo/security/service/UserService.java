package com.yibo.security.service;

import com.yibo.security.entity.UserAuthorization;
import com.yibo.security.entity.UserEntity;

public interface UserService {
    UserEntity findUserByUsername(String username);

    void insertUser(UserEntity userEntity);

    UserAuthorization getUserAuthorization(String username);
}