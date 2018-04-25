package com.yibo.security.exception;

public class TokenException extends RuntimeException {

    private static final long serialVersionUID = 6929553949220355312L;

    public TokenException(String message) {
        super(message);
    }
}