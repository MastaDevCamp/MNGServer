package com.masta.cms.auth.exception;

import com.masta.core.response.DefaultRes;
import com.masta.core.response.ResponseMessage;
import com.masta.core.response.StatusCode;
import io.jsonwebtoken.JwtException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class AuthControllerAdvice {
    @ExceptionHandler({UserForbiddenException.class, JwtException.class,
            InvalidJwtAuthenticationException.class, InvalidJwtFormException.class})
    @ResponseStatus(value = HttpStatus.OK)
    @ResponseBody
    public ResponseEntity catchException(Exception e){
        log.error(e.getMessage());
        return new ResponseEntity(DefaultRes.res(StatusCode.OK, ResponseMessage.Forbidden_User), HttpStatus.OK);
    }
}
