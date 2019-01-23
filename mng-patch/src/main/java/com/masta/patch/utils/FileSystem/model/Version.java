package com.masta.patch.utils.FileSystem.model;

import lombok.Builder;
import lombok.Getter;


@Getter
@Builder
public class Version{
    private String from;
    private String to;
}
