package com.masta.auth.membership.controller;

import com.masta.auth.exception.AlreadyExistsException;
import com.masta.auth.membership.entity.NonSocialUser;
import com.masta.auth.membership.repository.NonSocialUserRepository;
import com.masta.auth.membership.service.NonSocialService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/member")
public class MembershipController {


    @Autowired
    NonSocialService nonSocialService;

    @Autowired
    NonSocialUserRepository nonSocialUserRepository;

    @GetMapping("/test")
    public String test(){
        return "hello";
    }

    /**
     * Social Login이 아닐 때 사용할 계정 회원가입 기능입니다.
     *
     * @param nonSocialUser
     * @return  localhost:8080/ 으로 Redirect
     */
    @PostMapping("/join")
    public String create(@RequestBody NonSocialUser nonSocialUser) {

        // 존재하는지 확인
        NonSocialUser newUser = nonSocialUserRepository.findByUsername(nonSocialUser.getUsername());
        if (newUser != null) {
            throw new AlreadyExistsException("이미 존재합니다.");
        }

        // 이메일 인증했는지 확인

        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();            // 비밀번호 암호화
        nonSocialUser.setPassword(passwordEncoder.encode(nonSocialUser.getPassword()));
        nonSocialService.createUser(nonSocialUser);             // DB에 생성
        return "redirect:/";
    }

//    @PostMapping("/login")
//    public User login(@RequestBody LoginReq loginReq){
//        Authentication authentication = authenticationManager.
//    }
}
