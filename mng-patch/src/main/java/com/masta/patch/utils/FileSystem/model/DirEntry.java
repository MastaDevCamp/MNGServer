package com.masta.patch.utils.FileSystem.model;

import com.fasterxml.jackson.annotation.JsonView;
import lombok.*;

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

    public List<DirEntry> dirEntryList;

    public List<FileEntry> fileEntryList;
}
