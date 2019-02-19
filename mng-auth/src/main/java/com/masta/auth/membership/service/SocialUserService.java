package com.masta.auth.membership.service;


import com.masta.auth.config.YmlConfig;
import com.masta.auth.exception.ExceptionMessage;
import com.masta.auth.exception.exceptions.InternalServerException;
import com.masta.auth.membership.dto.SocialUserForm;
import com.masta.auth.membership.entity.SocialUser;
import com.masta.auth.membership.repository.SocialUserRepository;
import com.masta.auth.oauth2.response.AuthToken;
import com.masta.auth.oauth2.response.UserData;
import com.masta.core.response.ResponseMessage;
import lombok.extern.slf4j.Slf4j;
import org.omg.IOP.ExceptionDetailMessage;
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

import javax.annotation.PostConstruct;
import java.util.Optional;

@Slf4j
@Component
public class SocialUserService {

    private final SocialUserRepository socialUserRepository;
    private final String fbaseUrl = "https://graph.facebook.com/";
    private String gclientId;
    private String gclientSecret;
    private String fclientId;
    private String fclientSecret;

    @Autowired
    RestTemplate restTemplate;

    @Autowired
    YmlConfig ymlConfig;

    public SocialUserService(SocialUserRepository socialUserRepository) {
        this.socialUserRepository = socialUserRepository;
    }

    @PostConstruct
    protected void init() {
        fclientId = ymlConfig.getFacebook().getClientId();
        fclientSecret = ymlConfig.getFacebook().getClientSecret();
        gclientId = ymlConfig.getGoogle().getClientId();
        gclientSecret = ymlConfig.getGoogle().getClientSecret();
    }

    @Transactional
    public SocialUser getOrSave(SocialUserForm socialUserForm) {
        SocialUser user = new SocialUser();
        Optional<SocialUser> saveUser = socialUserRepository.findBySocialId(socialUserForm.getSocial_id());
        if (!saveUser.isPresent()) {
            user = socialUserForm.toEntity();
            user.setAuthority("ROLE_USER");
            user = socialUserRepository.save(user);
        } else user = saveUser.get();
        return user;
    }

    public UserData getSocialUser(String token, String provider) {
        String clientId = null, clientSecret = null, getAccessUrl = null;
        ResponseEntity<AuthToken> appAccessToken = null;
        if (provider.equals("facebook")) {
            clientId = fclientId;
            clientSecret = fclientSecret;
            getAccessUrl = fbaseUrl + "oauth/access_token";
        } else {
            clientId = gclientId;
            clientSecret = gclientSecret;
        }
        String appToken = getAccessAppToken(getAccessUrl, clientId, clientSecret);
        if (appToken== null)
            throw new InternalServerException(ResponseMessage.FAILED_GET_APP_TOKEN);
        UserData userData = getUserInfo(token, appToken);

        log.info(userData.getData().getUser_id());
        if (userData.getData().getUser_id() == null)
            throw new InternalServerException(ResponseMessage.FAILED_GET_SOCIALUSERINFO);
        return userData;
    }

    public UserData getUserInfo(String userToken, String AppToken){
        UriComponentsBuilder getUserInfoUrl = UriComponentsBuilder.fromHttpUrl(fbaseUrl + "debug_token")
                .queryParam("input_token", userToken)
                .queryParam("access_token", AppToken);

        ResponseEntity<UserData> userInfo = restTemplate.exchange(
                getUserInfoUrl.toUriString(),
                HttpMethod.GET,
                new HttpEntity<>(new HttpHeaders()),
                new ParameterizedTypeReference<UserData>() {
                }
        );
        return userInfo.getBody();
    }

    public String getAccessAppToken(String getAccessUrl, String clientId, String clientSecret){
        UriComponentsBuilder getAccessTokenUrl = UriComponentsBuilder.fromHttpUrl(getAccessUrl)
                .queryParam("client_id", clientId)
                .queryParam("client_secret", clientSecret)
                .queryParam("grant_type", "client_credentials");

        ResponseEntity<AuthToken> appAccessToken = restTemplate.exchange(
                getAccessTokenUrl.toUriString(),
                HttpMethod.GET,
                new HttpEntity<>(new HttpHeaders()),
                new ParameterizedTypeReference<AuthToken>() {}
        );
        return appAccessToken.getBody().getAccess_token();
    }


}
