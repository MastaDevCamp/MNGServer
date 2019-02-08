package com.masta.auth.oauth2.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @RequestMapping("/me")
    public Principal user(Principal principal) {

        return principal;
    }

}
