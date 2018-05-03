package com.yibo.security;

import com.yibo.security.entity.UserAuthorization;
import com.yibo.security.utils.JSONUtil;
import org.junit.Test;

import java.util.HashSet;
import java.util.Set;

public class JsonTest {
    @Test
    public void test() {
        UserAuthorization userAuthorization = new UserAuthorization();
        userAuthorization.setUsername("12312");
        Set<String> roleList = new HashSet<>();
        roleList.add("121");
        roleList.add("42542");
        roleList.add("132121");
        Set<String> resourceList = new HashSet<>(roleList);
        userAuthorization.setRoleList(roleList);
        userAuthorization.setResourceList(resourceList);
        String json = JSONUtil.toJson(userAuthorization);
        System.out.println(json);
        System.out.println("------");
        UserAuthorization authorization = JSONUtil.toObject(json, UserAuthorization.class);
        assert authorization != null;
        System.out.println(authorization.getUsername());
    }
}