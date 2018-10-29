package com.yibo.security.filter;

import com.yibo.security.exception.TokenException;
import io.jsonwebtoken.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import static com.yibo.security.constants.TokenConstant.*;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

public class JWTAuthenticationFilter extends BasicAuthenticationFilter {
    private static final Logger LOGGER = LoggerFactory.getLogger(JWTAuthenticationFilter.class);
    private static StringRedisTemplate stringRedisTemplate;

    public static void setRedisTemplate(StringRedisTemplate stringRedisTemplate) {
        JWTAuthenticationFilter.stringRedisTemplate = stringRedisTemplate;
    }

    public JWTAuthenticationFilter(AuthenticationManager authenticationManager) {
        super(authenticationManager);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        String token = request.getHeader(AUTHORIZATION);
        if (token == null || !token.startsWith(BEARER)) {
            chain.doFilter(request, response);
            return;
        }
        UsernamePasswordAuthenticationToken authentication = getAuthentication(request);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        chain.doFilter(request, response);
    }

    @SuppressWarnings({"unchecked"})
    private UsernamePasswordAuthenticationToken getAuthentication(HttpServletRequest request) {
        String token = request.getHeader(AUTHORIZATION);
        if (token != null) {
            token = token.replace(BEARER, "");
            // parse the token.
            try {
                ValueOperations<String, String> ops = stringRedisTemplate.opsForValue();
                String signingKey = ops.get(token);
                if (null == signingKey) {
                    throw new TokenException("Token已过期");
                }
                Claims claims = Jwts.parser()
                        .setSigningKey(signingKey)
                        .parseClaimsJws(token)
                        .getBody();
                String user = claims.getSubject();
                LOGGER.info("用户名:{}", user);
                if (user != null) {
                    String redisToken = ops.get(user);
                    if (redisToken == null) {
                        throw new TokenException("Token已过期");
                    }
                    if (!redisToken.equals(token)) {
                        throw new TokenException("Token信息不一致");
                    }
                    stringRedisTemplate.expire(user, TOKEN_REDIS_EXPIRATION, TimeUnit.SECONDS);
                    stringRedisTemplate.expire(token, TOKEN_REDIS_EXPIRATION, TimeUnit.SECONDS);
                    //获取用户对权限列表
                    List<String> roleList = (List<String>) claims.get(ROLE_LIST);
                    List<String> resourceList = (List<String>) claims.get(RESOURCE_LIST);
                    Set<GrantedAuthority> grantedAuthorities = new HashSet<>();
                    for (String role : roleList) {
                        grantedAuthorities.add(new SimpleGrantedAuthority("ROLE_" + role));
                    }
                    for (String resource : resourceList) {
                        grantedAuthorities.add(new SimpleGrantedAuthority(resource));
                    }
                    return new UsernamePasswordAuthenticationToken(user, null, grantedAuthorities);
                }
            } catch (ExpiredJwtException e) {
                LOGGER.error("Token已过期: {} " + e);
                throw new TokenException("Token已过期");
            } catch (UnsupportedJwtException e) {
                LOGGER.error("Token格式错误: {} " + e);
                throw new TokenException("Token格式错误");
            } catch (MalformedJwtException e) {
                LOGGER.error("Token没有被正确构造: {} " + e);
                throw new TokenException("Token没有被正确构造");
            } catch (SignatureException e) {
                LOGGER.error("签名失败: {} " + e);
                throw new TokenException("签名失败");
            }
        }
        return null;
    }
}