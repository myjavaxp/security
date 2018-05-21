package com.yibo.security;

import com.yibo.security.dao.UserDao;
import com.yibo.security.dao.UserRoleDao;
import com.yibo.security.entity.SysUser;
import com.yibo.security.entity.UserEntity;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class DaoTest {
    @Resource
    private UserDao userDao;
    @Resource
    private UserRoleDao userRoleDao;

    @Test
    public void test01() {
        SysUser user = userDao.findUserDetailsByUserId(1L);
        System.out.println(user);
        System.out.println(userDao.findUserDetailsByUserId(1L));
    }

    @Test
    public void test02() {
        List<Long> roleIds = new ArrayList<>();
        roleIds.add(1L);
        roleIds.add(2L);
        userRoleDao.getResourcesByRoleIds(roleIds).forEach(System.out::println);
    }

    @Test
    public void test03() {
        List<UserEntity> userEntityList = new ArrayList<>();
        userEntityList.add(new UserEntity(null, "test01", "123456", "test@qq.com"));
        userEntityList.add(new UserEntity(null, "test02", "123456", "test@qq.com"));
        userEntityList.add(new UserEntity(null, "test03", "123456", "test@qq.com"));
        userEntityList.add(new UserEntity(null, "test04", "123456", "test@qq.com"));
        int i = userDao.insertUsers(userEntityList);
        System.out.println(i);
    }

    @Test
    public void test04() {
        UserEntity user1 = userDao.selectByPrimaryKey(1L);
        UserEntity user2 = userDao.selectByPrimaryKey(1L);
        System.out.println(user1==user2);
    }
}