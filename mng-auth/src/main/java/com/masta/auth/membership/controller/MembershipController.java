package com.masta.auth.membership.controller;

import com.masta.auth.config.jwt.JwtTokenProvider;
import com.masta.auth.membership.dto.LoginRes;
import com.masta.auth.membership.dto.UserForm;
import com.masta.auth.membership.entity.NonSocialUser;
import com.masta.auth.membership.repository.NonSocialUserRepository;
import com.masta.auth.membership.service.NonSocialService;
import com.masta.core.response.DefaultRes;
import com.masta.core.response.ResponseMessage;
import com.masta.core.response.StatusCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;

import static com.masta.core.response.DefaultRes.FAIL_DEFAULT_RES;

@Slf4j
@RestController
@RequestMapping(value = "/member")
public class MembershipController {


    @Autowired
    NonSocialService nonSocialService;

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    JwtTokenProvider jwtTokenProvider;

    @GetMapping("/test")
    public String test(){
        return "hello";
    }

    /**
     * Social Login이 아닐 때 사용할 계정 회원가입 기능입니다.
     *
     * @param userForm
     * @return  if success HttpStatus.OK return
     */
    @PostMapping("/join")
    public ResponseEntity create(@RequestBody UserForm userForm) {
        try {
            NonSocialUser newUser = nonSocialService.getUser(userForm.getUsername());
            if (newUser != null) {
                return new ResponseEntity(DefaultRes.res(StatusCode.OK, ResponseMessage.EXIST_USER), HttpStatus.OK);
            }
            // + 이메일 인증했는지 확인
            nonSocialService.createUser(userForm.toNonSocailUser());
            return new ResponseEntity(HttpStatus.OK);
        }catch (Exception e){
            log.error(e.getMessage());
            return new ResponseEntity<>(FAIL_DEFAULT_RES, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * account login function.
     * @param userForm(String username, String password)
     * @return if login success return LoginRes(String username, Long userNum)
     */
    @PostMapping("/login")
    public ResponseEntity login(@RequestBody UserForm userForm){
           try{
               Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(userForm.getUsername(), userForm.getPassword()));
               NonSocialUser nonSocialUser = nonSocialService.getUser(userForm.getUsername());
               String token = jwtTokenProvider.createToken(nonSocialUser.getNum(),nonSocialUser.getAuthority());
               LoginRes loginRes = LoginRes.builder()
                       .userNum(nonSocialUser.getNum())
                       .token(token)
                       .build();
               // + NEED JWT PROVIDER
               return new ResponseEntity(loginRes, HttpStatus.OK);
           }catch(AuthenticationException e){
                log.error(e.getMessage());
               return new ResponseEntity(DefaultRes.res(StatusCode.OK, ResponseMessage.INVALID_ID_OR_PW), HttpStatus.OK);
           }
           catch (Exception e){
               log.error(e.getMessage());
               return new ResponseEntity<>(FAIL_DEFAULT_RES, HttpStatus.INTERNAL_SERVER_ERROR);
           }
    }
}
