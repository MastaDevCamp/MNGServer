package com.masta.cms.auth.exception;


public class UserForbiddenException extends RuntimeException{
    public UserForbiddenException(String e) {
        super(e);
    }
}