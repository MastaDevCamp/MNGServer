package com.masta.auth.config;

import com.masta.auth.jwt.JwtConfigurer;
import com.masta.auth.jwt.JwtTokenProvider;
import com.masta.auth.membership.service.NonSocialService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import javax.servlet.Filter;

@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    NonSocialService nonSocialService;

    @Autowired
    Filter ssoFilter;

    @Autowired
    JwtTokenProvider jwtTokenProvider;

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                    .httpBasic().disable()
                    .csrf().disable()
                    .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                    .antMatcher("/**").authorizeRequests()
                    .antMatchers("/v2/api-docs", "/configuration/ui", "/swagger-resources", "/configuration/security", "/swagger-ui.html", "/webjars/**","/swagger-resources/configuration/ui","/swagger-ui.html").permitAll()
                    .antMatchers("/", "/login/kakao","/login/**", "/webjars/**", "/error**","/member/**").permitAll()
                    .antMatchers("/test/**").hasAnyAuthority("ROLE_USER")
                    .anyRequest().authenticated()
                .and()
                .logout().logoutSuccessUrl("/").permitAll()
                .and()
//                .csrf().csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
//                .and()
//                .formLogin()
//            .and()
                .logout()
//                .and()
//                .exceptionHandling().authenticationEntryPoint(new LoginUrlAuthenticationEntryPoint("/"))
                .and()
                .exceptionHandling()
                .authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED))
                .and()
                .apply(new JwtConfigurer(jwtTokenProvider))
                .and()
                    .addFilterBefore(ssoFilter, BasicAuthenticationFilter.class);
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(nonSocialService).passwordEncoder(nonSocialService.passwordEncoder());
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers("/v2/api-docs", "/configuration/ui", "/swagger-resources", "/configuration/security", "/swagger-ui.html", "/webjars/**");


    }


}
