package com.masta.auth.membership.entity;

import lombok.*;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import java.time.LocalDateTime;

@Entity
@DiscriminatorValue("social")
@Getter
@NoArgsConstructor(access = AccessLevel.PUBLIC)
public class SocialUser extends User{


    private String socialId;
    private String provider;
    private String tokenValue;


    @Builder
    public SocialUser(Long num, String authority, LocalDateTime createdAt, LocalDateTime updatedAt, String socialId, String provider, String tokenValue) {
        super(num, authority, createdAt, updatedAt);
        this.socialId = socialId;
        this.provider = provider;
        this.tokenValue = tokenValue;
    }
}
