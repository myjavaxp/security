package com.yibo.security;

import com.yibo.security.dao.UserDao;
import com.yibo.security.dao.UserRoleDao;
import com.yibo.security.entity.SysUser;
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
    public void test01(){
        SysUser user = userDao.findUserDetailsByUserId(1L);
        System.out.println(user);
    }
    @Test
    public void test02(){
        List<Long> roleIds=new ArrayList<>();
        roleIds.add(1L);
        roleIds.add(2L);
        userRoleDao.getResourcesByRoleIds(roleIds).forEach(System.out::println);
    }
}