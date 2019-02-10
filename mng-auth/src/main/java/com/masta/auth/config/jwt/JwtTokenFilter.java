package com.masta.auth.config.jwt;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.masta.auth.exception.InvalidJwtTokenException;
import io.jsonwebtoken.JwtException;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.GenericFilterBean;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;

public class JwtTokenFilter extends GenericFilterBean {
    private JwtTokenProvider jwtTokenProvider;
    public JwtTokenFilter(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }


    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain filterChain)
            throws IOException, ServletException {
        String token = jwtTokenProvider.resolveToken((HttpServletRequest) req);
        try {
            if (token != null && jwtTokenProvider.validateToken(token)) {
                Authentication auth = token != null ? jwtTokenProvider.getAuthentication(token) : null;
                SecurityContextHolder.getContext().setAuthentication(auth);
            }
        } catch (JwtException e) {
            ((HttpServletResponse)res).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            setErrorResponse(HttpStatus.FORBIDDEN,(HttpServletResponse) res,e);
            logger.error(e.getMessage());
            //((HttpServletResponse) res).sendError(HttpServletResponse.SC_UNAUTHORIZED,"Invalid token");
//            throw new InvalidJwtTokenException("Expired or invalid JWT token");
        } finally {
            filterChain.doFilter(req, res);
        }
    }


        public void setErrorResponse(HttpStatus status, HttpServletResponse response, Throwable ex){
            response.setStatus(status.value());
            response.setContentType("application/json");
            // A class used for errors
            ApiError apiError = new ApiError(status, ex);
            try {
                String json = apiError.convertToJson();
                System.out.println(json);
                response.getWriter().write(json);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        class ApiError {

            private HttpStatus status;
            @JsonSerialize(using = LocalDateTimeSerializer.class)
            @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy hh:mm:ss")
            private LocalDateTime timestamp;
            private String message;
            private String debugMessage;

            private ApiError() {
                timestamp = LocalDateTime.now();
            }
            public ApiError(HttpStatus status) {
                this();
                this.status = status;
            }

            public ApiError(HttpStatus status, Throwable ex) {
                this();
                this.status = status;
                this.message = "Invalid JWT";
                this.debugMessage = ex.getLocalizedMessage();
            }

            public ApiError(HttpStatus status, String message, Throwable ex) {
                this();
                this.status = status;
                this.message = message;
                this.debugMessage = ex.getLocalizedMessage();
            }

            public String convertToJson() throws JsonProcessingException {
                if (this == null) {
                    return null;
                }
                ObjectMapper mapper = new ObjectMapper();
                mapper.registerModule(new JavaTimeModule());
                mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

                return mapper.writeValueAsString(this);
            }

            public HttpStatus getStatus() {
                return status;
            }

            public void setStatus(HttpStatus status) {
                this.status = status;
            }

            public LocalDateTime getTimestamp() {
                return timestamp;
            }

            public void setTimestamp(LocalDateTime timestamp) {
                this.timestamp = timestamp;
            }

            public String getMessage() {
                return message;
            }

            public void setMessage(String message) {
                this.message = message;
            }

            public String getDebugMessage() {
                return debugMessage;
            }

            public void setDebugMessage(String debugMessage) {
                this.debugMessage = debugMessage;
            }
        }

}