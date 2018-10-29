package com.yibo.security.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yibo.security.entity.UserAuthorization;
import com.yibo.security.entity.UserEntity;
import com.yibo.security.service.UserService;
import com.yibo.security.utils.JSONUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.lang.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.yibo.security.constants.TokenConstant.*;
import static io.jsonwebtoken.SignatureAlgorithm.HS256;
import static java.util.Collections.emptyList;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;

public class JWTLoginFilter extends UsernamePasswordAuthenticationFilter {
    private static final Logger LOGGER = LoggerFactory.getLogger(JWTLoginFilter.class);
    private AuthenticationManager authenticationManager;

    private static StringRedisTemplate stringRedisTemplate;
    private static UserService userService;

    public JWTLoginFilter(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

    public static void setRedisTemplate(StringRedisTemplate stringRedisTemplate) {
        JWTLoginFilter.stringRedisTemplate = stringRedisTemplate;
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
    @SuppressWarnings({"unchecked"})
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException {
        String username = authResult.getName();
        ValueOperations<String, String> ops = stringRedisTemplate.opsForValue();
        String token = ops.get(username);
        String signingKey;
        UserAuthorization authorization;
        if (token != null) {
            signingKey = ops.get(token);
            try {
                Claims claims = Jwts.parser()
                        .setSigningKey(signingKey)
                        .parseClaimsJws(token)
                        .getBody();
                String user = claims.getSubject();
                Assert.isTrue(username.equals(user), "Token解析用户名不一致");
                List<String> roleList = (List<String>) claims.get(ROLE_LIST);
                List<String> resourceList = (List<String>) claims.get(RESOURCE_LIST);
                authorization = new UserAuthorization(username, new HashSet<>(roleList), new HashSet<>(resourceList));
                LOGGER.info("用户: ->{}<- 从Redis获取token", username);
            } catch (Exception e) {
                e.printStackTrace();//这个时候证明Redis中存储的Token有问题（过期或者被人篡改），所以需要重新设置
                stringRedisTemplate.delete(token);
                authorization = userService.getUserAuthorization(username);
                token = getToken(username, ops, authorization);
            }
        } else {
            authorization = userService.getUserAuthorization(username);
            token = getToken(username, ops, authorization);
        }
        stringRedisTemplate.expire(username, TOKEN_REDIS_EXPIRATION, TimeUnit.SECONDS);
        stringRedisTemplate.expire(token, TOKEN_REDIS_EXPIRATION, TimeUnit.SECONDS);
        response.addHeader(AUTHORIZATION, BEARER + token);
        response.setContentType(APPLICATION_JSON_UTF8_VALUE);
        PrintWriter writer = response.getWriter();
        String successContent = "{\"status\":\"success\",\"message\":" + JSONUtil.toJson(authorization) + "}";
        writer.write(successContent);
        writer.flush();
        writer.close();
    }

    private String getToken(String username, ValueOperations<String, String> ops, UserAuthorization authorization) {
        String signingKey = username + System.currentTimeMillis();
        Claims claims = Jwts.claims().setSubject(username);
        claims.put(ROLE_LIST, authorization.getRoleList());
        claims.put(RESOURCE_LIST, authorization.getResourceList());
        String token = Jwts.builder()
                .setClaims(claims)
                .setExpiration(new Date(System.currentTimeMillis() + TOKEN_EXPIRATION)) //过期时间15天
                .signWith(HS256, signingKey)
                .compact();
        ops.set(username, token);
        ops.set(token, signingKey);
        return token;
    }
}