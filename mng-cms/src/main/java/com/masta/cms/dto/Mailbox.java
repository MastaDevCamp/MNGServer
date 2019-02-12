package com.masta.cms.dto;

import lombok.Data;

import java.util.Date;

@Data
public class Mailbox {
    private int mail_id;
    private String title;
    private String contents;
    private String file_link;
    private Date post_at;
    private Date finish_at;
}
