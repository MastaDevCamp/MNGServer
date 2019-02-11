package com.masta.auth.config.Handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.masta.auth.config.jwt.JwtTokenProvider;
import com.masta.auth.membership.dto.SocialUserForm;
import com.masta.auth.membership.entity.SocialUser;
import com.masta.auth.membership.service.SocialService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
@Component
public class CommonSuccessComponent {

    private ObjectMapper objectMapper;
    private SocialService socialService;
    private JwtTokenProvider jwtTokenProvider;

    public CommonSuccessComponent(ObjectMapper objectMapper, SocialService socialService, JwtTokenProvider jwtTokenProvider) {
        this.objectMapper = objectMapper;
        this.socialService = socialService;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    public  void successProcess(HttpServletRequest request, HttpServletResponse response, Authentication authentication, String provider) throws IOException, ServletException {
        OAuth2Authentication oAuth2Authentication = (OAuth2Authentication) authentication;
        Object obj = ((OAuth2Authentication) authentication).getOAuth2Request();
        SocialUserForm socialUserForm = objectMapper.convertValue(oAuth2Authentication.getUserAuthentication().getDetails(), SocialUserForm.class);
        socialUserForm.setProvider(provider);
        SocialUser socialUser = socialService.getOrSave(socialUserForm);

        String token = jwtTokenProvider.createToken(socialUser.getNum(),"ROLE_USER");
        response.addHeader("Authorization", "Bearer "+token);


        response.sendRedirect("/auth/me/"+ socialUser.getNum());
    }

}
