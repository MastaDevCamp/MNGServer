package com.masta.auth.membership.controller;

import com.masta.auth.membership.service.AccountUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 단순 test API
 */
@RestController
@RequestMapping(value = "/test")
public class TestController {

    @Autowired
    AccountUserService accountUserService;

    /**
     * user 권한이 있는 멤버만 접근 가능합니다.
     * @return
     */
    @GetMapping("/test")
    public String test(){
        return "hello";
    }

    // + admin 만 접근 가능한 test api 생성하기.
}
