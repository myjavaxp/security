package com.yibo.security.filter;

import com.yibo.security.constants.TokenConstant;
import com.yibo.security.entity.Resource;
import com.yibo.security.entity.Role;
import com.yibo.security.entity.UserEntity;
import com.yibo.security.exception.TokenException;
import com.yibo.security.service.ResourceService;
import com.yibo.security.service.RoleService;
import com.yibo.security.service.UserService;
import io.jsonwebtoken.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class JWTAuthenticationFilter extends BasicAuthenticationFilter {
    private static final Logger LOGGER = LoggerFactory.getLogger(JWTAuthenticationFilter.class);
    private static ResourceService resourceService;
    private static RoleService roleService;
    private static UserService userService;
    private static JedisPool jedisPool;

    public static void setResourceService(ResourceService resourceService) {
        JWTAuthenticationFilter.resourceService = resourceService;
    }

    public static void setRoleService(RoleService roleService) {
        JWTAuthenticationFilter.roleService = roleService;
    }

    public static void setUserService(UserService userService) {
        JWTAuthenticationFilter.userService = userService;
    }

    public static void setJedisPool(JedisPool jedisPool) {
        JWTAuthenticationFilter.jedisPool = jedisPool;
    }

    public JWTAuthenticationFilter(AuthenticationManager authenticationManager) {
        super(authenticationManager);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        String token = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (token == null || !token.startsWith(TokenConstant.BEARER)) {
            chain.doFilter(request, response);
            return;
        }
        UsernamePasswordAuthenticationToken authentication = getAuthentication(request);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        chain.doFilter(request, response);
    }

    private UsernamePasswordAuthenticationToken getAuthentication(HttpServletRequest request) {
        String token = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (token != null) {
            token = token.replace(TokenConstant.BEARER, "");
            // parse the token.
            try (Jedis jedis = jedisPool.getResource()) {
                String signingKey = jedis.get(token);
                if (null == signingKey) {
                    throw new TokenException("Token已过期");
                }
                String user = Jwts.parser()
                        .setSigningKey(signingKey)
                        .parseClaimsJws(token)
                        .getBody()
                        .getSubject();
                LOGGER.info("用户名:{}", user);
                if (user != null) {
                    if (!jedis.exists(user)) {
                        throw new TokenException("Token已过期");
                    }
                    if (!jedis.get(user).equals(token)) {
                        throw new TokenException("Token信息不一致");
                    }
                    jedis.expire(user, TokenConstant.TOKEN_REDIS_EXPIRATION);
                    jedis.expire(token, TokenConstant.TOKEN_REDIS_EXPIRATION);
                    //获取用户对权限列表
                    //这块可以想办法优化一下，减少对数据库对读写
                    UserEntity userEntity = userService.findUserByUsername(user);
                    Set<GrantedAuthority> grantedAuthorities = new HashSet<>();
                    List<Role> roles = roleService.getRoleValuesByUserId(userEntity.getId());
                    for (Role role : roles) {
                        GrantedAuthority grantedAuthority = new SimpleGrantedAuthority("ROLE_" + role.getName());
                        grantedAuthorities.add(grantedAuthority);
                        List<Resource> resources = resourceService.getPermissionsByRoleId(role.getId());
                        for (Resource resource : resources) {
                            grantedAuthorities.add(new SimpleGrantedAuthority(resource.getUrl()));
                        }
                    }
                    return new UsernamePasswordAuthenticationToken(userEntity.getUsername(), userEntity.getPassword(), grantedAuthorities);
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