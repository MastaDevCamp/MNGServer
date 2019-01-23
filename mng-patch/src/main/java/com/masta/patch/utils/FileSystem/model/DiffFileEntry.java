package com.masta.patch.utils.FileSystem.model;

import lombok.*;

import java.io.File;

/**
 * diffType list
 * 'c' : CREATE
 * 'u' : UPDATE
 * 'd' : DELETE
 */

@Getter
public class DiffFileEntry extends FileEntry {
    private char diffType;

    @Builder
    public DiffFileEntry(int listIndex, char type, String path, Version version, int fileIndex, String compress, int originalSize, int compressSize, String originalHash, String compressHash, char diffType){
        super(listIndex, type, path, version, fileIndex, compress, originalSize, compressSize, originalHash, compressHash);
        this.diffType = diffType;
    }
}

