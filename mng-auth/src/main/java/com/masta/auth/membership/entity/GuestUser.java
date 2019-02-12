package com.masta.auth.membership.entity;

import lombok.Builder;
import lombok.Getter;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
@Getter
public class GuestUser extends User{
    @Builder
    public GuestUser(){

    }
}
