package com.masta.patch.utils;

import com.masta.patch.model.DirEntry;
import com.masta.patch.model.FileEntry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;

@Component
@Slf4j
public class EntrySystem {

    @Autowired
    private HashingSystem hashingSystem;

    @Autowired
    private Compress compress;


    private final String COMPRESS_TYPE = "zip";

    /**
     * @param file
     * @return DirEntry Filled fields.
     */
    public DirEntry getDirEntry(File file, String version) {
        char fileType = 'D';

        DirEntry dirEntry = DirEntry.builder()
                .type(fileType)
                .path(file.getPath())
                .compress(COMPRESS_TYPE)
                .diffType('x') //patch
                .version(version) //patch
                .build();

        return dirEntry;
    }


    /**
     * @param file
     * @return FileEntry Filled fields.
     */
    public FileEntry getFileEntry(File file, String version) {

        String compressFilePath = compress.zip(file);
        File compressFile = new File(compressFilePath);

        char fileType = file.getTotalSpace() != 0 ? 'F' : 'G';

        FileEntry fileEntry = FileEntry.builder()
                .type(fileType)
                .path(file.getPath())
                .compress(COMPRESS_TYPE)
                .originalSize((int) file.length())
                .compressSize((int) compressFile.length())
                .originalHash(hashingSystem.MD5Hashing(file))
                .compressHash(hashingSystem.MD5Hashing(compressFile))
                .diffType('x') //patch
                .version(version)
                .build();

        return fileEntry;
    }

}
