package com.masta.auth.oauth2.Handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.masta.auth.jwt.JwtTokenProvider;
import com.masta.auth.membership.dto.SocialUserForm;
import com.masta.auth.membership.entity.SocialUser;
import com.masta.auth.membership.service.SocialUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;

@Slf4j
@Component
public class CommonSuccessComponent {

    private ObjectMapper objectMapper;
    private SocialUserService socialUserService;
    private JwtTokenProvider jwtTokenProvider;

    public CommonSuccessComponent(ObjectMapper objectMapper, SocialUserService socialUserService, JwtTokenProvider jwtTokenProvider) {
        this.objectMapper = objectMapper;
        this.socialUserService = socialUserService;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    public  void successProcess(HttpServletRequest request, HttpServletResponse response, Authentication authentication, String provider) throws IOException, ServletException {
        OAuth2Authentication oAuth2Authentication = (OAuth2Authentication) authentication;
        SocialUserForm socialUserForm = objectMapper.convertValue(oAuth2Authentication.getUserAuthentication().getDetails(), SocialUserForm.class);
        socialUserForm.setProvider(provider);
        SocialUser socialUser = socialUserService.getOrSave(socialUserForm);

        String token ="Bearer "+ jwtTokenProvider.createToken(socialUser.getNum(),"ROLE_USER");

        Cookie cookie  = new Cookie("Authorization", URLEncoder.encode(token,"UTF-8").replace("+","%20"));
        response.addCookie(cookie);
        response.setStatus(200);
    }

}
