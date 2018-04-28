package com.yibo.security.controller;

import com.yibo.security.constants.TokenConstant;
import io.jsonwebtoken.Jwts;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@RestController
public class LogoutController {
    private static final Logger LOGGER = LoggerFactory.getLogger(LogoutController.class);
    @Resource
    private JedisPool jedisPool;

    @GetMapping("/logout")
    public Map<String, String> logout(HttpServletRequest request) {
        String token = request.getHeader(HttpHeaders.AUTHORIZATION);
        Map<String, String> map = new HashMap<>();
        if (null != token) {
            LOGGER.info("有token登出");
            token = token.replace(TokenConstant.BEARER, "");
            try {
                Jedis jedis = jedisPool.getResource();
                String signingKey = jedis.get(token);
                String user = Jwts.parser()
                        .setSigningKey(signingKey)
                        .parseClaimsJws(token)
                        .getBody()
                        .getSubject();
                if (jedis.exists(user)) {
                    LOGGER.info("删除Redis登录信息");
                    jedis.del(user);
                    jedis.del(token);
                }
            } catch (Exception e) {
                LOGGER.info("Token信息有误");
                e.printStackTrace();
                map.put("status", "200");
                map.put("message", "登出成功");
                return map;
            }
        }
        map.put("status", "200");
        map.put("message", "登出成功");
        return map;
    }
}