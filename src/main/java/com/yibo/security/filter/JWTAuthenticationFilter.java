package com.yibo.security.filter;

import com.yibo.security.constants.EncodeConstant;
import com.yibo.security.entity.Role;
import com.yibo.security.entity.UserEntity;
import com.yibo.security.service.ResourceService;
import com.yibo.security.service.RoleService;
import com.yibo.security.service.UserService;
import io.jsonwebtoken.Jwts;
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

public class JWTAuthenticationFilter extends BasicAuthenticationFilter {
    private static ResourceService resourceService;
    private static RoleService roleService;
    private static UserService userService;

    public static void setResourceService(ResourceService resourceService) {
        JWTAuthenticationFilter.resourceService = resourceService;
    }

    public static void setRoleService(RoleService roleService) {
        JWTAuthenticationFilter.roleService = roleService;
    }

    public static void setUserService(UserService userService) {
        JWTAuthenticationFilter.userService = userService;
    }

    public JWTAuthenticationFilter(AuthenticationManager authenticationManager) {
        super(authenticationManager);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        String header = request.getHeader("Authorization");
        if (header == null || !header.startsWith("Bearer ")) {
            chain.doFilter(request, response);
            return;
        }
        UsernamePasswordAuthenticationToken authentication = getAuthentication(request);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        chain.doFilter(request, response);
    }

    private UsernamePasswordAuthenticationToken getAuthentication(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        if (token != null) {
            // parse the token.
            String user = Jwts.parser()
                    .setSigningKey(EncodeConstant.SIGNING_KEY)
                    .parseClaimsJws(token.replace("Bearer ", ""))
                    .getBody()
                    .getSubject();
            if (user != null) {
                UserEntity userEntity = userService.findUserByUsername(user);
                Set<GrantedAuthority> grantedAuthorities = new HashSet<>();
                List<Role> roles = roleService.getRoleValuesByUserId(userEntity.getId());
                for (Role role : roles) {
                    GrantedAuthority grantedAuthority = new SimpleGrantedAuthority("ROLE_" + role.getName());
                    grantedAuthorities.add(grantedAuthority);
                    resourceService.getPermissionsByRoleId(role.getId()).forEach(
                            resource -> grantedAuthorities.add(new SimpleGrantedAuthority(resource.getUrl())));
                }
                return new UsernamePasswordAuthenticationToken(userEntity.getUsername(), userEntity.getPassword(), grantedAuthorities);
            }
            return null;
        }
        return null;
    }
}