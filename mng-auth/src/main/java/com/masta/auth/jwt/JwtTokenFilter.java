package com.masta.auth.jwt;

import com.masta.auth.exception.ApiError;
import com.masta.auth.exception.exceptions.InvalidJwtAuthenticationException;
import io.jsonwebtoken.JwtException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
public class JwtTokenFilter extends GenericFilterBean {
    private JwtTokenProvider jwtTokenProvider;

    public JwtTokenFilter(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }


    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain filterChain)
            throws IOException, ServletException {
        String url = ((HttpServletRequest) req).getRequestURL().toString();
        if (url.equals("http://localhost:8080/login/kakao") || url.equals("http://localhost:8080/")) {
            filterChain.doFilter(req, res);
        } else {
            String token = jwtTokenProvider.resolveToken((HttpServletRequest) req);
            try {
                if (token != null && jwtTokenProvider.validateToken(token)) {
                    Authentication auth = token != null ? jwtTokenProvider.getAuthentication(token) : null;
                    SecurityContextHolder.getContext().setAuthentication(auth);
                }
            } catch (JwtException e) {
                ((HttpServletResponse) res).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                logger.error(e.getMessage());
                setErrorResponse(HttpStatus.FORBIDDEN, (HttpServletResponse) res, e, "Invalid Jwt");
            } catch (InvalidJwtAuthenticationException e) {
                ((HttpServletResponse) res).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                logger.error(e.getMessage());
                setErrorResponse(HttpStatus.FORBIDDEN, (HttpServletResponse) res, e, "Invalid Jwt");
            } finally {
                filterChain.doFilter(req, res);
            }
        }
    }


    public void setErrorResponse(HttpStatus status, HttpServletResponse response, Throwable ex, String message) {
        response.setStatus(status.value());
        response.setContentType("application/json");
        ApiError apiError = new ApiError(status, ex, message);
        try {
            String json = apiError.convertToJson();
            response.getWriter().write(json);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}