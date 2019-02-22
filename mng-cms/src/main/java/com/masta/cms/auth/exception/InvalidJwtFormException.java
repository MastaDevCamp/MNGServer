package com.masta.cms.auth.exception;


public class InvalidJwtFormException extends RuntimeException{
    public InvalidJwtFormException(String e) {
        super(e);
    }
}