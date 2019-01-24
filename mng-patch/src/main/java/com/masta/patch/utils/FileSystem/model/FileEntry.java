package com.masta.patch.utils.FileSystem.model;

import com.fasterxml.jackson.annotation.JsonView;
import lombok.*;

@Getter
@AllArgsConstructor
@Builder
public class FileEntry {
    @JsonView(Views.Full.class)

    private int listIndex;
    @JsonView(Views.Full.class)
    private char type;
    @JsonView(Views.Full.class)
    private String path;
    @JsonView(Views.Full.class)
    private Version version;
    @JsonView(Views.Full.class)
    private int fileIndex;
    @JsonView(Views.Full.class)
    private String compress;
    @JsonView(Views.Full.class)
    private int originalSize;
    @JsonView(Views.Full.class)
    private int compressSize;
    @JsonView(Views.Full.class)
    private String originalHash;
    @JsonView(Views.Full.class)
    private String compressHash;

    @JsonView(Views.Patch.class)
    private char diffType; // 이걸로 나누기!!
}






