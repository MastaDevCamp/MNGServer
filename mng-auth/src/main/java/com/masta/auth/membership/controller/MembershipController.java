package com.masta.auth.membership.controller;

import com.masta.auth.membership.entity.NonSocialUser;
import com.masta.auth.membership.service.NonSocialService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/member")
public class MembershipController {


    @Autowired
    NonSocialService nonSocialService;

    @GetMapping("/test")
    public String test(){
        return "hello";
    }

    //user 회원가입 만들기....
    @GetMapping("/{id}/{pw}")
    public void Logintest(@PathVariable String id, @PathVariable String pw){
        NonSocialUser nonSocialUser = NonSocialUser.builder()
                .password(pw)
                .username(id)
                .build();
        nonSocialService.createUser(nonSocialUser);
    }

//    @PostMapping("/login")
//    public User login(@RequestBody LoginReq loginReq){
//        Authentication authentication = authenticationManager.
//    }
}
