package com.masta.patch.utils.FileSystem;

import com.masta.patch.utils.FileSystem.model.FileEntry;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class FileSystem {

    private List<FileEntry> fileList;

    public FileSystem(final List<FileEntry> fileList) {
        this.fileList = fileList;
    }

    public void listFilesForFolder(final File folder) {
        for (final File fileEntry : folder.listFiles()) {
            if (fileEntry.isDirectory()) {
                listFilesForFolder(fileEntry);
            } else {
                fileList.add(FileEntry.builder().path(fileEntry.getPath()).build());
            }
        }
    }

    public List<String> getFileTreeList(String path) {
        listFilesForFolder(new File(path));
        List<String> rtn = new ArrayList<>();
        for (FileEntry fileEntry : fileList) {
            rtn.add(fileEntry.getFileInfo());
        }
        log.info(path);
        return rtn;
    }
}
