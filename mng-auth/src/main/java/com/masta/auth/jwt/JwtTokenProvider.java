package com.masta.auth.jwt;

import com.masta.auth.config.YmlConfig;
import com.masta.auth.exception.ExceptionMessage;
import com.masta.auth.exception.exceptions.InvalidJwtAuthenticationException;
import com.masta.auth.membership.dto.UserDetailsDTO;
import com.masta.auth.membership.repository.UserRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class JwtTokenProvider {

    @Autowired
    YmlConfig ymlConfig;

    private String secretKey;


    private long validityInMilliseconds = 259200000;//3600000*24*3; // 1h*hour*day

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private UserRepository userRepository;

    @PostConstruct
    protected void init() {
        secretKey = Base64.getEncoder().encodeToString(ymlConfig.getJwt().getSecret().getBytes());
    }

    public String createToken(Long usernum, String roles) {
        Claims claims = Jwts.claims().setSubject(usernum.toString());
        claims.put("roles", roles);
        Date now = new Date();
        System.out.println(validityInMilliseconds);
        Date validity = new Date(now.getTime() + validityInMilliseconds);
        System.out.println(validity);
        String jwts = Jwts.builder()//
                .setClaims(claims)//
                .setIssuedAt(now)//
                .setExpiration(validity)//
                .signWith(SignatureAlgorithm.HS256, secretKey)//
                .compact();
        redisTemplate.opsForValue().set(usernum.toString(), jwts);
        redisTemplate.expire(usernum.toString(),3, TimeUnit.DAYS);
        return jwts;
    }

    public Authentication getAuthentication(String token) {
        String userrole = (String) Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody().get("roles");
        Long usernum = Long.parseLong(Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody().getSubject());
        UserDetailsDTO userDetailsDTO = UserDetailsDTO.builder().authority(userrole).usernum(usernum.toString()).build();
        ArrayList<GrantedAuthority> auth = new ArrayList<>();
        auth.add(new SimpleGrantedAuthority(userrole));
        return new UsernamePasswordAuthenticationToken(userDetailsDTO, "", auth);
    }

    public String getUsername(String token) {
        return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody().getSubject();
    }
    public String resolveToken(HttpServletRequest req) {
        String bearerToken = req.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7, bearerToken.length());
        }
        return null;
    }

    public boolean validateToken(String token) {
        String usernum = getUsername(token);
        String redistoken = (String) redisTemplate.opsForValue().get(usernum);
        if (token.equals(redistoken)) return true;
        else {
            throw new InvalidJwtAuthenticationException(ExceptionMessage.INVALID_JWT_TOKEN);
        }
    }

}