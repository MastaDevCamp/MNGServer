package com.masta.auth.exception.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class NoSuchDataException extends RuntimeException{
    public NoSuchDataException(String message) {
        super(message);
    }
}
