package com.masta.auth.config.Handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.masta.auth.membership.dto.SocialUserForm;
import com.masta.auth.membership.service.SocialService;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;


@Component
public class FacebookSuccessHandler implements AuthenticationSuccessHandler {
    private HttpSession httpSession;
    private ObjectMapper objectMapper;
    private SocialService socialService;

    public FacebookSuccessHandler(HttpSession httpSession, ObjectMapper objectMapper, SocialService socialService) {
        this.httpSession = httpSession;
        this.objectMapper = objectMapper;
        this.socialService = socialService;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {

        OAuth2Authentication oAuth2Authentication = (OAuth2Authentication) authentication;
        Object obj = ((OAuth2Authentication) authentication).getOAuth2Request();
        SocialUserForm socialUserForm = objectMapper.convertValue(oAuth2Authentication.getUserAuthentication().getDetails(), SocialUserForm.class);
        socialUserForm.setProvider("facebook");
        socialService.getOrSave(socialUserForm);
        response.sendRedirect("/auth/me");
    }

}
