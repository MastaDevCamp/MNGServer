package com.masta.patch.utils.FileSystem.model;

import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class DirEntry {
    private char type;          //고정값
    private String path;
    private String compress;    // 고정값
    private String version;
    private char diffType; //patch

    public List<DirEntry> dirEntryList = new ArrayList<>();

    public List<FileEntry> fileEntryList = new ArrayList<>();

    public void setDiffType(char diffType) {
        this.diffType = diffType;
    }

    public void setAllDiffType(char diffType) {

        for (FileEntry fileEntry : this.fileEntryList) {
            fileEntry.setDiffType(diffType);
        }
        for (DirEntry dirEntry : this.dirEntryList) {
            dirEntry.setDiffType(diffType);
            dirEntry.setAllDiffType(diffType);
        }
    }

    public FileEntry findFileEntry(String path) {
        FileEntry fileEntry = fileEntryList.stream().filter(f -> f.getPath().equals(path)).findAny().orElse(null);
        return fileEntry;
    }
}
