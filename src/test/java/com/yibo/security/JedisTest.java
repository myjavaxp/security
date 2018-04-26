package com.yibo.security;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import javax.annotation.Resource;
import java.util.Set;

@RunWith(SpringRunner.class)
@SpringBootTest
public class JedisTest {
    @Resource
    private JedisPool jedisPool;
    @Test
    public void test(){
        Jedis jedis = jedisPool.getResource();
        Set<String> keys = jedis.keys("*");
        System.out.println(keys.size());
        jedis.close();
    }
}