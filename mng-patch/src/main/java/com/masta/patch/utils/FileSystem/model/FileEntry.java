package com.masta.patch.utils.FileSystem.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FileEntry {
    private int listIndex;
    private char type;
    private String path;
    private String version;
    private int fileIndex;
    private String compress;
    private int originalSize;
    private int compressSize;
    private String originalHash;
    private String compressHash;

}
