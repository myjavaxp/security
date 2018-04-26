package com.yibo.security.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yibo.security.constants.EncodeConstant;
import com.yibo.security.entity.UserEntity;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.http.MediaType;
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

import static java.util.Collections.emptyList;

public class JWTLoginFilter extends UsernamePasswordAuthenticationFilter {
    private AuthenticationManager authenticationManager;

    public JWTLoginFilter(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
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
        String token = Jwts.builder()
                .setSubject(authResult.getName())
                .setExpiration(new Date(System.currentTimeMillis() + 60 * 1000)) //过期时间60秒
                .signWith(SignatureAlgorithm.HS256, EncodeConstant.SIGNING_KEY)
                .compact();
        response.addHeader("Authorization", "Bearer " + token);
        response.setContentType(MediaType.APPLICATION_JSON_UTF8_VALUE);
        PrintWriter writer = response.getWriter();
        String successContent = "{\"status\":\"success\",\"message\":" + "\"这里放前台需要的权限列表\"" + "}";
        writer.write(successContent);
        writer.flush();
        writer.close();
    }
}