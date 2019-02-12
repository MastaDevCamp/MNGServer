package com.masta.cms.model;

import lombok.Data;

import java.util.Date;

@Data
public class NoticeReq {
    private int type;
    private String title;
    private String contents;
    private Date finish_at;
    private String file_link;
}
