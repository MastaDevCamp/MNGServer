package com.masta.patch.utils.FileSystem.model;

import lombok.*;

@Setter
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

    public String print() {
        return String.format("%c | %s | %s | %d | %d | %s | %s | %s | %c ",
                this.type, this.path, this.compress, this.originalSize, this.compressSize, this.originalHash, this.compressHash, this.version, this.diffType);
    }
}