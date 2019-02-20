package com.masta.cms.Push;

import lombok.Data;

@Data
public class FcmReq {
    private String type;
    private String objIdx;
    private String title;
    private String body;
}
