//package com.masta.auth.oauth2.Handler;
//
//import org.springframework.security.core.Authentication;
//import org.springframework.security.web.authentication.logout.SimpleUrlLogoutSuccessHandler;
//import org.springframework.stereotype.Component;
//
//import javax.servlet.ServletException;
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//import java.io.IOException;
//
//@Component
//public class LogoutSuccessHandler extends SimpleUrlLogoutSuccessHandler {
//    @Override
//    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
//        if(authentication!=null){
//            logger.info(authentication);
//        }
//        super.onLogoutSuccess(request, response, authentication);
//    }
//}
