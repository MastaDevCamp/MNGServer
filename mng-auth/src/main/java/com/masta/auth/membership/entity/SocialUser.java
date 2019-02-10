package com.masta.auth.membership.entity;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue("social")
@Getter
@NoArgsConstructor(access = AccessLevel.PUBLIC)
public class SocialUser extends User{

    private String socialId;
    private String provider;
    private String tokenValue;

    @Builder
    public SocialUser(String socialId, String provider, String tokenValue) {
        this.socialId = socialId;
        this.provider = provider;
        this.tokenValue = tokenValue;
    }
}
