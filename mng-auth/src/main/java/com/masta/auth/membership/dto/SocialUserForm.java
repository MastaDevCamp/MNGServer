package com.masta.auth.membership.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.masta.auth.membership.entity.SocialUser;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@NoArgsConstructor
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class SocialUserForm implements Serializable {

    @JsonProperty("id")
    private String social_id;
    @JsonProperty("provider")
    private String provider;
    @JsonProperty("token")
    private String token;


    public void setProvider(String provider) {
        this.provider = provider;
    }

    public SocialUser toEntity(){
        return SocialUser.builder()
                .provider(provider)
                .socialId(social_id)
                .tokenValue(token)
                .build();
    }
}
