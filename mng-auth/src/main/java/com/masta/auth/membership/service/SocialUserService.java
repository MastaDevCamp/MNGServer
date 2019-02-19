package com.masta.auth.membership.service;


import com.masta.auth.membership.dto.SocialUserForm;
import com.masta.auth.membership.entity.SocialUser;
import com.masta.auth.membership.repository.SocialUserRepository;
import com.masta.auth.oauth2.response.AuthToken;
import com.masta.auth.oauth2.response.UserData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Optional;

@Slf4j
@Component
public class SocialUserService {

    private final SocialUserRepository socialUserRepository;
    private final String baseUrl = "https://graph.facebook.com/";
    private final String clientId =  "371797430298075";
    private final String clientSecret = "8a61c627f56a738bd8d609ade91e04bf";

    @Autowired
    RestTemplate restTemplate;

    public SocialUserService(SocialUserRepository socialUserRepository) {
        this.socialUserRepository = socialUserRepository;
    }

    @Transactional
    public SocialUser getOrSave(SocialUserForm socialUserForm) {
        SocialUser user = new SocialUser();
        Optional<SocialUser> saveUser = socialUserRepository.findBySocialId(socialUserForm.getSocial_id());
        if (!saveUser.isPresent()) {
            user = socialUserForm.toEntity();
            user.setAuthority("ROLE_USER");
            user = socialUserRepository.save(user);
        }
        else user=saveUser.get();
        return user;
    }

    public UserData getSocialUser(String token){
        UriComponentsBuilder getAccessTokenUrl = UriComponentsBuilder.fromHttpUrl(baseUrl+"oauth/access_token")
                .queryParam("client_id",clientId)
                .queryParam("client_secret",clientSecret)
                .queryParam("grant_type","client_credentials");

        ResponseEntity<AuthToken> appAccessToken = restTemplate.exchange(
                getAccessTokenUrl.toUriString(),
                HttpMethod.GET,
                new HttpEntity<>(new HttpHeaders()),
                new ParameterizedTypeReference<AuthToken>(){}
        );
        log.info(appAccessToken.getBody().getAccess_token());

        UriComponentsBuilder getUserInfoUrl = UriComponentsBuilder.fromHttpUrl(baseUrl+"debug_token")
                .queryParam("input_token",token)
                .queryParam("access_token",appAccessToken.getBody().getAccess_token());

        ResponseEntity<UserData> userInfo= restTemplate.exchange(
                getUserInfoUrl.toUriString(),
                HttpMethod.GET,
                new HttpEntity<>(new HttpHeaders()),
                new ParameterizedTypeReference<UserData>(){}
        );
        log.info(userInfo.getBody().getData().getUser_id());
        return userInfo.getBody();
    }


}
