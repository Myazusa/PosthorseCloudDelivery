package com.github.myazusa.posthorseclouddelivery.core.exception;

public class AuthUserException extends RuntimeException {
    public AuthUserException() {
        super("用户名或密码错误");
    }
    public AuthUserException(String message) {
        super(message);
    }
}
