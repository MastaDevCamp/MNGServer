package com.masta.cms.auth.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class UserDto {
    private Long usernum;
    private String role;

    @Builder
    public UserDto(Long usernum, String role) {
        this.usernum = usernum;
        this.role = role;
    }
}
