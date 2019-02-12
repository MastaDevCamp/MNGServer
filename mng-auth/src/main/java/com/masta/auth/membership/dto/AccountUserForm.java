package com.masta.auth.membership.dto;

import com.masta.auth.membership.entity.AccountUser;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class AccountUserForm {
    private String username;
    private String password;

    public AccountUser toNonSocailUser(){
        return AccountUser.builder()
                .username(username)
                .password(password)
                .authority("ROLE_USER")
                .build();
    }
}
