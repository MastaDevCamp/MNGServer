package com.masta.auth.membership.entity;


import com.masta.auth.membership.dto.LoginRes;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Transient;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;

@Entity
@DiscriminatorValue("account")
@Getter
@NoArgsConstructor(access = AccessLevel.PUBLIC)
//@ToString
public class AccountUser extends User implements UserDetails {

    private String username;
    private String password;
    private String email;
    private boolean emailConfirm;
    @Transient
    private Collection<? extends GrantedAuthority> authorities;

    public void setPassword(String password) {
        this.password = password;
    }

    public void setAuthorities(Collection<? extends GrantedAuthority> authorities) {
        this.authorities = authorities;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        ArrayList<GrantedAuthority> auth = new ArrayList<>();
        auth.add(new SimpleGrantedAuthority(super.getAuthority()));
        return auth;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Builder
    public AccountUser(Long num, String authority, LocalDateTime createdAt, LocalDateTime updatedAt, String username, String password, String email, boolean emailConfirm) {
        super(num, authority, createdAt, updatedAt);
        this.username = username;
        this.password = password;
        this.email = email;
        this.emailConfirm = emailConfirm;
    }

    public LoginRes toLoginRes(){
        return LoginRes.builder()
                .userNum(getNum())
                .build();
    }
}
