package com.masta.cms.dto;

import lombok.Data;

import java.util.Date;

@Data
public class Choice {
    private int choice_id;
    private int prfm_id;
    private int script;
    private int answer;
    private Date choice_at;
}
