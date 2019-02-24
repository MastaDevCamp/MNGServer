package com.masta.cms.auth.jwt;

import com.masta.cms.auth.config.JwtConfig;
import com.masta.cms.auth.dto.UserDto;
import com.masta.cms.auth.exception.UserForbiddenException;
import com.masta.cms.auth.exception.InvalidJwtFormException;
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

    public UserDto getUser(String token, String auth) {
        token = resolveToken(token);
        if(token == null) throw new InvalidJwtFormException(ResponseMessage.INVALID_JWT_FORM);
        String userrole = (String) Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody().get("roles");
        if (!userrole.equals(auth))
            throw new UserForbiddenException(ResponseMessage.Forbidden_User);
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


}

