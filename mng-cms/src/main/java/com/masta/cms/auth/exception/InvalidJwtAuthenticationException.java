package com.masta.cms.auth.exception;


public class InvalidJwtAuthenticationException extends RuntimeException{
    public InvalidJwtAuthenticationException(String e) {
        super(e);
    }
}