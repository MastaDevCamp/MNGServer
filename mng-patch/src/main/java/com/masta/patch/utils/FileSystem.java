package com.masta.patch.utils;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.List;

@Slf4j
@Service
public class FileSystem {

    private List<String> fileList;

    public FileSystem(final List<String> fileList) {
        this.fileList = fileList;
    }

    public void listFilesForFolder(final File folder) {
        for (final File fileEntry : folder.listFiles()) {
            if (fileEntry.isDirectory()) {
                listFilesForFolder(fileEntry);
            } else {
                fileList.add(fileEntry.getPath() + fileEntry.getName());
            }
        }
    }

    public List<String> getFileTreeList(String path) {
        listFilesForFolder(new File(path));
        log.info(path);
        return fileList;
    }
}
