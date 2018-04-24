package com.yibo.security.service.impl;

import com.yibo.security.entity.Resource;
import com.yibo.security.entity.Role;
import com.yibo.security.entity.UserEntity;
import com.yibo.security.service.ResourceService;
import com.yibo.security.service.RoleService;
import com.yibo.security.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    private final UserService userService;
    private final RoleService roleService;
    private final ResourceService resourceService;

    @Autowired
    public UserDetailsServiceImpl(UserService userService, RoleService roleService, ResourceService resourceService) {
        this.userService = userService;
        this.roleService = roleService;
        this.resourceService = resourceService;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserEntity userEntity = userService.findUserByUsername(username);
        if (null == userEntity) {
            throw new UsernameNotFoundException("用户:" + username + ",不存在!");
        }
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
        return new User(
                userEntity.getUsername(),
                userEntity.getPassword(),
                true,//这四个哥们可以做成动态赋值
                true,
                true,
                true,
                grantedAuthorities);
    }
}