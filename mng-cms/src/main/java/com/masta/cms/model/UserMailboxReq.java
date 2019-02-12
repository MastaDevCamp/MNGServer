package com.masta.cms.model;

import lombok.Data;

@Data
public class UserMailboxReq {
    private int umailbox_id;
    private int user_id;
    private int mailbox_id;
}
