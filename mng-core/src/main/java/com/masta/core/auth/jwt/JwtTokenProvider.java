package com.masta.core.auth.jwt;

import com.masta.core.auth.dto.UserDto;
import com.masta.core.auth.config.JwtConfig;
import com.masta.core.auth.jwt.exception.InvalidJwtAuthenticationException;
import com.masta.core.auth.jwt.exception.InvalidJwtFormException;
import com.masta.core.response.ResponseMessage;
import io.jsonwebtoken.Jwts;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Base64;

@Slf4j
@Component
public class JwtTokenProvider {

    @Autowired
    JwtConfig jwtConfig;

    private String secretKey;

    @Value("${security.jwt.token.expire-length:3600000}")
    private long validityInMilliseconds = 3600000; // 1h

    @Autowired
    private RedisTemplate redisTemplate;

    @PostConstruct
    protected void init() {
        secretKey = Base64.getEncoder().encodeToString(jwtConfig.getSecret().getBytes());
    }

    public UserDto getUser(String token) {
        String userrole = (String) Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody().get("roles");
        Long usernum = Long.parseLong(Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody().getSubject());
        return UserDto.builder()
                .role(userrole)
                .usernum(usernum)
                .build();
    }

    public String resolveToken(String bearerToken) {
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7, bearerToken.length());
        }
        return null;
    }

    public UserDto validateToken(String token) {
        token = resolveToken(token);
        if(token==null) throw new InvalidJwtFormException(ResponseMessage.INVALID_JWT_FORM);
        UserDto userDto = getUser(token);
        String redistoken = (String) redisTemplate.opsForValue().get(userDto.getUsernum().toString());
        if (token.equals(redistoken))
            return userDto;
        else
            throw new InvalidJwtAuthenticationException(ResponseMessage.INVALID_TOKEN);
    }

}

