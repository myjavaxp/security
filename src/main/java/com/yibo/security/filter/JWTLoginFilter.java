package com.yibo.security.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yibo.security.constants.TokenConstant;
import com.yibo.security.entity.UserAuthorization;
import com.yibo.security.entity.UserEntity;
import com.yibo.security.service.UserService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.lang.Assert;
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
    private static UserService userService;

    public JWTLoginFilter(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

    public static void setJedisPool(JedisPool jedisPool) {
        JWTLoginFilter.jedisPool = jedisPool;
    }

    public static void setUserService(UserService userService) {
        JWTLoginFilter.userService = userService;
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
        String signingKey;
        if (jedis.exists(username)) {
            token = jedis.get(username);
            signingKey = jedis.get(token);
            try {
                String user = Jwts.parser()
                        .setSigningKey(signingKey)
                        .parseClaimsJws(token)
                        .getBody()
                        .getSubject();
                Assert.isTrue(username.equals(user), "Token解析用户名不一致");
                LOGGER.info("用户: ->{}<- 从Redis获取token", username);
            } catch (Exception e) {
                e.printStackTrace();//这个时候证明Redis中存储的Token有问题（过期或者被人篡改），所以需要重新设置
                jedis.del(token);
                token = getToken(username, jedis);
            }
        } else {
            token = getToken(username, jedis);
        }
        jedis.expire(username, TokenConstant.TOKEN_REDIS_EXPIRATION);
        jedis.expire(token, TokenConstant.TOKEN_REDIS_EXPIRATION);
        jedis.close();
        response.addHeader(HttpHeaders.AUTHORIZATION, TokenConstant.BEARER + token);
        response.setContentType(MediaType.APPLICATION_JSON_UTF8_VALUE);
        PrintWriter writer = response.getWriter();
        String successContent = "{\"status\":\"success\",\"message\":" + "\"这里放前台需要的权限列表\"" + "}";
        writer.write(successContent);
        writer.flush();
        writer.close();
    }

    private String getToken(String username, Jedis jedis) {
        String signingKey = username + System.currentTimeMillis();
        UserAuthorization authorization = userService.getUserAuthorization(username);
        Claims claims = Jwts.claims().setSubject(username);
        claims.put("roleList", authorization.getRoleList());
        claims.put("resourceList", authorization.getResourceList());
        String token = Jwts.builder()
                .setClaims(claims)
                .setExpiration(new Date(System.currentTimeMillis() + TokenConstant.TOKEN_EXPIRATION)) //过期时间15天
                .signWith(SignatureAlgorithm.HS256, signingKey)
                .compact();
        jedis.set(username, token);
        jedis.set(token, signingKey);
        return token;
    }
}