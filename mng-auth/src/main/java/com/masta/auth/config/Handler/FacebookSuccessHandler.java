package com.masta.auth.config.Handler;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


@Component
public class FacebookSuccessHandler implements AuthenticationSuccessHandler {
    private CommonSuccessComponent commonSuccessComponent;

    public FacebookSuccessHandler(CommonSuccessComponent commonSuccessComponent) {
        this.commonSuccessComponent = commonSuccessComponent;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        commonSuccessComponent.successProcess(request, response,authentication,"kakao");
    }
}
