package com.masta.auth.oauth2.controller;

import com.masta.auth.config.jwt.JwtTokenProvider;
import com.masta.auth.membership.dto.LoginRes;
import com.masta.core.auth.dto.UserDto;
import com.masta.core.auth.jwt.exception.InvalidJwtAuthenticationException;
import com.masta.core.auth.jwt.exception.InvalidJwtFormException;
import com.masta.core.response.DefaultRes;
import com.masta.core.response.ResponseMessage;
import com.masta.core.response.StatusCode;
import io.jsonwebtoken.JwtException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.HashMap;
import java.util.Map;

import static com.masta.core.response.DefaultRes.FAIL_DEFAULT_RES;
import static java.util.stream.Collectors.toList;
import static org.springframework.http.ResponseEntity.ok;

@Slf4j
@RestController
@RequestMapping("/auth")
public class AuthController {

    private JwtTokenProvider jwtTokenProvider;

    public AuthController(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @GetMapping("/me/{usernum}")
    public ResponseEntity user(Principal principal, @PathVariable Long usernum) {
        String token = jwtTokenProvider.createToken(usernum,"ROLE_USER");
        LoginRes loginRes  = LoginRes.builder().userNum(usernum).token(token).build();
        return new ResponseEntity(loginRes, HttpStatus.OK);
    }

    @GetMapping("/me")
    public ResponseEntity currentUser(@AuthenticationPrincipal UserDetails userDetails){
        Map<Object, Object> model = new HashMap<>();
        model.put("usernum", userDetails.getUsername());
        model.put("roles", userDetails.getAuthorities()
                .stream()
                .map(a -> ((GrantedAuthority) a).getAuthority())
                .collect(toList())
        );
        return ok(model);
    }

}
