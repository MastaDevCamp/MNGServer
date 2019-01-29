package com.masta.patch.utils.FileSystem.model;

import lombok.*;

@Getter
@AllArgsConstructor
@Builder
@NoArgsConstructor
@ToString
public class FileEntry {
    private char type;          //고정값
    private String path;
    private String compress;    //고정값
    private int originalSize;
    private int compressSize;
    private String originalHash;
    private String compressHash;
    private String version;

    private char diffType;      //c, d, u //parch

    public void setDiffType(char diffType) {
        this.diffType = diffType;
    }
}






