package com.yibo.security.constants;

public class TokenConstant {
    public static final String BEARER = "Bearer ";
    public static final int TOKEN_REDIS_EXPIRATION = 30 * 60;
    public static final long TOKEN_EXPIRATION = 15 * 24 * 60 * 60 * 1000L;
    public static final String ROLE_LIST = "roleList";
    public static final String RESOURCE_LIST = "resourceList";
}