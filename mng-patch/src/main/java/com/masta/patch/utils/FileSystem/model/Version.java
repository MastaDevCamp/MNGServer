package com.masta.patch.utils.FileSystem.model;

import com.fasterxml.jackson.annotation.JsonView;
import lombok.Builder;
import lombok.Getter;


@Getter
@Builder
public class Version{
    @JsonView(Views.Full.class)
    private String from;
    @JsonView(Views.Patch.class)
    private String to;
}
