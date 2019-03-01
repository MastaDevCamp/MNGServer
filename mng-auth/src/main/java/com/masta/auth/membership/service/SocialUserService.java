package com.masta.auth.membership.service;


import com.masta.auth.config.YmlConfig;
import com.masta.auth.exception.ExceptionMessage;
import com.masta.auth.exception.exceptions.InternalServerException;
import com.masta.auth.membership.dto.SocialUserForm;
import com.masta.auth.membership.entity.SocialUser;
import com.masta.auth.membership.repository.SocialUserRepository;
import com.masta.auth.oauth2.response.AuthToken;
import com.masta.auth.oauth2.response.UserData;
import com.masta.auth.oauth2.response.UserId;
import com.masta.core.response.ResponseMessage;
import lombok.extern.slf4j.Slf4j;
import org.omg.IOP.ExceptionDetailMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
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
    private final String gUrl = "https://www.googleapis.com/oauth2/v2/userinfo";
    private String gclientId;
    private String gclientSecret;
    private String fclientId;
    private String fclientSecret;

    @Autowired
    RestTemplate restTemplate;

    @Autowired
    YmlConfig ymlConfig;

    HttpService httpService = new HttpService();

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
    public SocialUser getOrSave(SocialUserForm socialUserForm) throws JSONException {
        SocialUser user = null;
        Optional<SocialUser> saveUser = socialUserRepository.findBySocialId(socialUserForm.getSocial_id());
        if (!saveUser.isPresent()) {
            user = socialUserForm.toEntity();
            user.setAuthority("ROLE_USER");
            user = socialUserRepository.save(user);
            Long num = saveUser.get().getNum();
            httpService.InitCMS("http://175.210.61.143.:8201/init", num);
        } else user = saveUser.get();
        return user;
    }

    public SocialUserForm getFacebookUser(String token) {
        ResponseEntity<AuthToken> appAccessToken = null;

        String clientId = fclientId;
        String clientSecret = fclientSecret;
        String getAccessUrl = fbaseUrl + "oauth/access_token";

        String appToken = getAccessAppToken(getAccessUrl, clientId, clientSecret);
        if (appToken == null) throw new InternalServerException(ResponseMessage.FAILED_GET_APP_TOKEN);
        UserData userData = getUserInfo(token, appToken);

        log.info(userData.getData().getUser_id());
        if (userData.getData().getUser_id() == null) throw new InternalServerException(ResponseMessage.FAILED_GET_SOCIALUSERINFO);

        SocialUserForm socialUserForm = SocialUserForm.builder()
                .social_id(userData.getData().getUser_id())
                .provider("facebook")
                .token(token)
                .build();
        return socialUserForm;
    }

    public SocialUserForm getGoogleUser(String token){
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization","Bearer "+token);
        ResponseEntity<UserId> userInfo = restTemplate.exchange(
                gUrl,
                HttpMethod.GET,
                new HttpEntity<>(headers),
                new ParameterizedTypeReference<UserId>() {
                }
        );

        SocialUserForm socialUserForm = SocialUserForm.builder()
                .social_id(userInfo.getBody().getId())
                .provider("google")
                .token(token)
                .build();
        return socialUserForm;
    }

    private UserData getUserInfo(String userToken, String AppToken) {
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

    private String getAccessAppToken(String getAccessUrl, String clientId, String clientSecret) {
        UriComponentsBuilder getAccessTokenUrl = UriComponentsBuilder.fromHttpUrl(getAccessUrl)
                .queryParam("client_id", clientId)
                .queryParam("client_secret", clientSecret)
                .queryParam("grant_type", "client_credentials");

        ResponseEntity<AuthToken> appAccessToken = restTemplate.exchange(
                getAccessTokenUrl.toUriString(),
                HttpMethod.GET,
                new HttpEntity<>(new HttpHeaders()),
                new ParameterizedTypeReference<AuthToken>() {
                }
        );
        return appAccessToken.getBody().getAccess_token();
    }


}
