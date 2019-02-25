package com.masta.cms.model;

import lombok.Data;

import java.util.Date;

@Data
public class UserReq {
    private int gold;
    private int ruby;
    private int pushonoff;
    private int heart;
    private Date charge_at;
    private int reset;
}
