package com.masta.auth.membership.dto;

import com.masta.auth.membership.entity.NonSocialUser;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class UserForm {
    private String username;
    private String password;

    public NonSocialUser toNonSocailUser(){
        return NonSocialUser.builder()
                .username(username)
                .password(password)
                .build();
    }
}
