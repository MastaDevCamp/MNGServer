package com.masta.cms.dto;

import lombok.Data;

import java.util.Date;

@Data
public class UserDetail {
    private int uid;
    private String nickname;
    private String photo;
    private int pushonoff; //0 : off, 1 : on
    private int gold;
    private int ruby;
    private int heart;
    private Date charge_at;
}
