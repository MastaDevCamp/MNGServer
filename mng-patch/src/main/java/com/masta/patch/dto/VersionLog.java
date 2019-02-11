package com.masta.patch.dto;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class VersionLog {
    int id;
    String version;
    String patch;
    String full;
}
