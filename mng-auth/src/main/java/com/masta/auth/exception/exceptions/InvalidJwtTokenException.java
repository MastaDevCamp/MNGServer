package com.masta.auth.exception.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

// 이미 존재
@SuppressWarnings("serial")
@ResponseStatus(value = HttpStatus.FORBIDDEN)
public class InvalidJwtTokenException extends RuntimeException{
    public InvalidJwtTokenException(String message){
        super(message);
    }
}
