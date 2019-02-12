package com.masta.cms.model;

import lombok.Data;

@Data
public class MailboxReq {
    private String title;
    private String contents;
    private String finish_at;
//    private Date finish_at;
}
