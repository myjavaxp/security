package com.yibo.security.entity;

import java.io.Serializable;
import java.util.Set;

public class UserAuthorization implements Serializable {
    private static final long serialVersionUID = -2452736519985134915L;
    private String username;
    private Set<String> roleList;
    private Set<String> resourceList;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Set<String> getRoleList() {
        return roleList;
    }

    public void setRoleList(Set<String> roleList) {
        this.roleList = roleList;
    }

    public Set<String> getResourceList() {
        return resourceList;
    }

    public void setResourceList(Set<String> resourceList) {
        this.resourceList = resourceList;
    }
}