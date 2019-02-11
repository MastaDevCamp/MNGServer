package com.masta.core;

import com.masta.core.auth.dto.UserDto;
import com.masta.core.auth.jwt.JwtTokenProvider;
import com.masta.core.auth.jwt.exception.InvalidJwtAuthenticationException;
import com.masta.core.auth.jwt.exception.InvalidJwtFormException;
import com.masta.core.response.DefaultRes;
import com.masta.core.response.ResponseMessage;
import com.masta.core.response.StatusCode;
import io.jsonwebtoken.JwtException;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import static com.masta.core.response.DefaultRes.FAIL_DEFAULT_RES;

@Slf4j
@RestController
public class TestController {

    private Logger logger = LoggerFactory.getLogger("test Controller logger");

    @Autowired
    JwtTokenProvider jwtTokenProvider;

    @Autowired
    RedisTemplate redisTemplate;

    @GetMapping("/test")
    public ResponseEntity test(@RequestHeader(value="Authentiation") String authentication){

        try {
            UserDto userDto =jwtTokenProvider.validateToken(authentication);
            return new ResponseEntity(userDto, HttpStatus.OK);
        }
        catch (JwtException|InvalidJwtAuthenticationException e){
            return new ResponseEntity(DefaultRes.res(StatusCode.OK, ResponseMessage.INVALID_TOKEN), HttpStatus.OK);
        }
        catch (InvalidJwtFormException e){
            return new ResponseEntity(DefaultRes.res(StatusCode.OK, ResponseMessage.INVALID_JWT_FORM), HttpStatus.OK);
        }
        catch (Exception e){
            log.error(e.getMessage());
            return new ResponseEntity<>(FAIL_DEFAULT_RES, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}