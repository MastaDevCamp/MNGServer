package com.masta.auth.membership.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import java.time.LocalDateTime;

@Entity
@Getter
@DiscriminatorValue("guest")
@NoArgsConstructor
//@ToString
public class GuestUser extends User{

    private String guestId;

    public void setGuestId(String guestId) {
        this.guestId = guestId;
    }

    @Builder
    public GuestUser(Long num, String authority, LocalDateTime createdAt, LocalDateTime updatedAt, String guestId) {
        super(num, authority, createdAt, updatedAt);
        this.guestId = guestId;
    }

}
