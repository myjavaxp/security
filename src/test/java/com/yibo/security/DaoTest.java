package com.yibo.security;

import com.yibo.security.dao.UserDao;
import com.yibo.security.entity.SysUser;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

@RunWith(SpringRunner.class)
@SpringBootTest
public class DaoTest {
    @Resource
    private UserDao userDao;
    @Test
    public void test(){
        SysUser user = userDao.findUserDetailsByUserId(1L);
        System.out.println(user);
    }
}