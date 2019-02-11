package com.masta.core.auth.jwt.exception;


public class InvalidJwtFormException extends RuntimeException{
    public InvalidJwtFormException(String e) {
        super(e);
    }
}