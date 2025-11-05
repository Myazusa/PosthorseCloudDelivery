package com.github.myazusa.posthorseclouddelivery.core.exception;

public class InvalidTokenException extends RuntimeException{
    public InvalidTokenException(){
        super("无效的Token，可能错误或已过期");
    }
}
