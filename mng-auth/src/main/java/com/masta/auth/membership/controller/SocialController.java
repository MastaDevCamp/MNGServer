package com.masta.auth.membership.controller;

import com.masta.auth.exception.exceptions.InternalServerException;
import com.masta.auth.jwt.JwtTokenProvider;
import com.masta.auth.membership.dto.SocialUserForm;
import com.masta.auth.membership.entity.SocialUser;
import com.masta.auth.membership.service.SocialUserService;
import com.masta.auth.oauth2.response.UserData;
import com.masta.core.response.DefaultRes;
import com.masta.core.response.StatusCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.masta.core.response.DefaultRes.FAIL_DEFAULT_RES;

@Slf4j
@RestController
@RequestMapping("/social")
public class SocialController {

    @Autowired
    private SocialUserService socialUserService;
    @Autowired
    private JwtTokenProvider jwtTokenProvider;


    @PostMapping("/login/facebook/{token}")
    public ResponseEntity facebookLogin(@PathVariable String token) {

        try {
            UserData userData = socialUserService.getSocialUser(token, "facebook");
            SocialUser socialUser = socialUserService.getOrSave(SocialUserForm.builder()
                    .social_id(userData.getData().getUser_id())
                    .provider("facebook")
                    .token(token)
                    .build()
            );
            String jwt = "Bearer " + jwtTokenProvider.createToken(socialUser.getNum(), "ROLE_USER");
            return new ResponseEntity(jwt, HttpStatus.OK);
        }catch (InternalServerException e){
            log.error(e.getMessage());
            return new ResponseEntity(DefaultRes.res(StatusCode.OK, e.getMessage()), HttpStatus.OK);
        }
        catch (Exception e){
            log.error(e.getMessage());
            return new ResponseEntity<>(FAIL_DEFAULT_RES, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
