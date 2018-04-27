package com.yibo.security.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yibo.security.constants.EncodeConstant;
import com.yibo.security.entity.UserEntity;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;

import static java.util.Collections.emptyList;

public class JWTLoginFilter extends UsernamePasswordAuthenticationFilter {
    private static final Logger LOGGER = LoggerFactory.getLogger(JWTLoginFilter.class);
    private AuthenticationManager authenticationManager;

    private static JedisPool jedisPool;

    public JWTLoginFilter(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

    public static void setJedisPool(JedisPool jedisPool) {
        JWTLoginFilter.jedisPool = jedisPool;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        try {
            UserEntity userEntity = new ObjectMapper().readValue(request.getInputStream(), UserEntity.class);
            return authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            userEntity.getUsername(),
                            userEntity.getPassword(),
                            emptyList()
                    ));
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException {
        String username = authResult.getName();
        Jedis jedis = jedisPool.getResource();
        String token;
        if (jedis.exists(username)) {
            token = jedis.get(username);
            LOGGER.info("用户: ->{}<- 从Redis获取token", username);
        } else {
            token = Jwts.builder()
                    .setSubject(username)
                    .setExpiration(new Date(System.currentTimeMillis() + 15 * 24 * 60 * 60 * 1000)) //过期时间15天
                    .signWith(SignatureAlgorithm.HS256, EncodeConstant.SIGNING_KEY)
                    .compact();
            jedis.set(username, token);
        }
        jedis.expire(username, 5 * 60);
        jedis.close();
        response.addHeader(HttpHeaders.AUTHORIZATION, "Bearer " + token);
        response.setContentType(MediaType.APPLICATION_JSON_UTF8_VALUE);
        PrintWriter writer = response.getWriter();
        String successContent = "{\"status\":\"success\",\"message\":" + "\"这里放前台需要的权限列表\"" + "}";
        writer.write(successContent);
        writer.flush();
        writer.close();
    }
}