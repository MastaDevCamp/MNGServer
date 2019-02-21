package com.masta.patch.utils.FileSystem;

import com.masta.patch.model.DirEntry;
import com.masta.patch.model.FileEntry;
import com.masta.patch.utils.EntrySystem;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.ArrayList;

import static com.masta.patch.service.UploadService.resetDir;

@Slf4j
@Component
public class FullJsonMaker {


    @Value("{local.zipFile.path}")
    private String zipFilePath;

    @Autowired
    private EntrySystem entrySystem;
    /**
     * Be called from controller
     *
     * @param path
     * @return Full DirEntry (return all file paths' contents)
     */
    public DirEntry getFileTreeList(String path, String version) {
        DirEntry rootDir = entrySystem.getDirEntry(new File(path), version);
        listFilesForFolder(rootDir, version);
        makeRelativePath(rootDir.getPath(), rootDir);
        log.info("version " + rootDir.getVersion() + " created.");
        return rootDir;
    }

    /**
     * recursion code
     * filled the contents by, recursion and for statements repeated their childDir
     * by using getFileEntry and getDirEntry, filled all the file's properties on object.
     *
     * @param parentDir
     */
    public void listFilesForFolder(final DirEntry parentDir, final String version) {

        parentDir.fileEntryList = new ArrayList<>();
        parentDir.dirEntryList = new ArrayList<>();

        resetDir(zipFilePath);

        File file = new File(parentDir.getPath());

        for (final File children : file.listFiles()) {
            if (children.isFile()) { //file

                FileEntry childFile = entrySystem.getFileEntry(children, version); //childFile object setting
                parentDir.fileEntryList.add(childFile); //child

            } else if (children.isDirectory()) { //dir

                DirEntry childDir = entrySystem.getDirEntry(children, version);
                parentDir.dirEntryList.add(childDir);
                listFilesForFolder(childDir, version); //자식 dir
            }
        }
    }

    public void makeRelativePath(String rootPath, DirEntry parentDir) {
        parentDir.setPath(parentDir.getPath().replace(rootPath, ""));
        removeRootPath(parentDir, rootPath);
    }

    public void removeRootPath(DirEntry rootDir, String rootPath) {
        for (FileEntry fileEntry : rootDir.fileEntryList) {
            fileEntry.setPath(fileEntry.getPath().replace(rootPath, ""));
        }
        for (DirEntry dirEntry : rootDir.dirEntryList) {
            dirEntry.setPath(dirEntry.getPath().replace(rootPath, ""));
            removeRootPath(dirEntry, rootPath);
        }
    }

}
