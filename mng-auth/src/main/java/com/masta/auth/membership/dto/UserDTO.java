package com.masta.auth.membership.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class UserDTO {
    private Long usernum;
    private String authority;
    private String type;

    @Builder
    public UserDTO(Long usernum, String authority, String type) {
        this.usernum = usernum;
        this.authority = authority;
        this.type = type;
    }
}
