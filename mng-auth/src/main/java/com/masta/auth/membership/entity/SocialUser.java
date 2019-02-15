package com.masta.auth.membership.entity;

import lombok.*;

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
    public SocialUser(Long num, String authority, String socialId, String provider, String tokenValue) {
        super(num, authority);
        this.socialId = socialId;
        this.provider = provider;
        this.tokenValue = tokenValue;
    }
}
