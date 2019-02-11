package com.masta.core.auth.jwt.exception;


public class InvalidJwtAuthenticationException extends RuntimeException{
    public InvalidJwtAuthenticationException(String e) {
        super(e);
    }
}