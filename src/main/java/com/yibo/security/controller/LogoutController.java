package com.yibo.security.controller;

import com.yibo.security.aop.LoggerManager;
import com.yibo.security.constants.TokenConstant;
import io.jsonwebtoken.Jwts;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@RestController
public class LogoutController {
    private static final Logger LOGGER = LoggerFactory.getLogger(LogoutController.class);
    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @GetMapping("/logout")
    @LoggerManager(description = "用户登出")
    public Map<String, String> logout(HttpServletRequest request) {
        String token = request.getHeader(HttpHeaders.AUTHORIZATION);
        Map<String, String> map = new HashMap<>();
        if (null != token) {
            LOGGER.info("有token登出");
            token = token.replace(TokenConstant.BEARER, "");
            try {
                ValueOperations<String, String> ops = stringRedisTemplate.opsForValue();
                String signingKey = ops.get(token);
                String user = Jwts.parser()
                        .setSigningKey(signingKey)
                        .parseClaimsJws(token)
                        .getBody()
                        .getSubject();
                Boolean flag = stringRedisTemplate.hasKey(user);
                if (flag != null && flag) {
                    LOGGER.info("删除Redis登录信息");
                    stringRedisTemplate.delete(user);
                    stringRedisTemplate.delete(token);
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