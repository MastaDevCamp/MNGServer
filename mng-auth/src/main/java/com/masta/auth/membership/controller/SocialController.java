package com.masta.auth.membership.controller;

import com.masta.auth.membership.service.SocialUserService;
import com.masta.auth.oauth2.response.UserData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/social")
public class SocialController {

    @Autowired
    private SocialUserService socialUserService;

    @PostMapping("/login/facebook/{token}")
    public ResponseEntity facebookLogin(@PathVariable String token){
        UserData userData = socialUserService.getSocialUser(token);
        return new ResponseEntity(userData.getData().getUser_id(), HttpStatus.OK);
    }

}
