package com.masta.auth.membership.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@Getter
@DiscriminatorValue("guest")
@NoArgsConstructor
@ToString
public class GuestUser extends User{

    private String guestId;

    public void setGuestId(String guestId) {
        this.guestId = guestId;
    }

    @Builder
    public GuestUser(Long num, String authority, String guestId) {
        super(num, authority);
        this.guestId = guestId;
    }

}
