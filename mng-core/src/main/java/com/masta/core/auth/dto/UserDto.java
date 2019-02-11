package com.masta.core.auth.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
public class UserDto {
    private Long usernum;
    private String role;

    @Builder
    public UserDto(Long usernum, String role) {
        this.usernum = usernum;
        this.role = role;
    }
}
