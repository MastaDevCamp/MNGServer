package com.masta.patch.utils.FileSystem.model;

import com.fasterxml.jackson.annotation.JsonView;
import lombok.*;

@Getter
@AllArgsConstructor
@Builder
public class FileEntry {
    @JsonView(Views.ALL.class)
    private int listIndex;
    @JsonView(Views.ALL.class)
    private char type;
    @JsonView(Views.ALL.class)
    private String path;
    @JsonView(Views.ALL.class)
    private int fileIndex;
    @JsonView(Views.ALL.class)
    private String compress;
    @JsonView(Views.ALL.class)
    private int originalSize;
    @JsonView(Views.ALL.class)
    private int compressSize;
    @JsonView(Views.ALL.class)
    private String originalHash;
    @JsonView(Views.ALL.class)
    private String compressHash;

    @JsonView(Views.FULL.class)
    private String nowVersion;

    @JsonView(Views.Patch.class)
    private String fromVersion;
    @JsonView(Views.Patch.class)
    private String toVersion;

    @JsonView(Views.Patch.class)
    private char diffType; // 이걸로 나누기!!
}






