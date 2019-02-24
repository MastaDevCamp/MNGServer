package com.masta.patch.model;

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

    public String print() {
        return String.format("%c | %s | %s | %s | %c ", this.type, this.path, this.compress, this.version, this.diffType);
    }

    public DirEntry(DirEntry dirEntry) {
        this.type = dirEntry.type;
        this.path = dirEntry.path;
        this.compress = dirEntry.compress;
        this.version = dirEntry.version;
        this.diffType = dirEntry.diffType;

        this.dirEntryList.addAll(dirEntry.dirEntryList);

        this.fileEntryList.addAll(dirEntry.fileEntryList);
    }


}
