package com.masta.cms.dto;


import lombok.Data;

import java.util.Date;

@Data
public class Notice {
    private int notice_id;
    private String type;
    private String title;
    private String contents;
    private String filelink;
    private Date post_at;
    private Date finish_at;
    private Date begin_at;

}
