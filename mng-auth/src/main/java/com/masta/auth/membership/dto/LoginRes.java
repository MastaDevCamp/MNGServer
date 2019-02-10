package com.masta.auth.membership.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@Builder
public class LoginRes {
    private Long userNum;
    private String token;
}
