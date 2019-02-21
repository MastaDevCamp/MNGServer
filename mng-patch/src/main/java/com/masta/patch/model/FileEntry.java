package com.masta.patch.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;

@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
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

    public static final String compressType = "zip";

    public String print() {
        return String.format("%c | %s | %s | %d | %d | %s | %s | %s | %c ",
                this.type, this.path, this.compress, this.originalSize, this.compressSize, this.originalHash, this.compressHash, this.version, this.diffType);
    }

}