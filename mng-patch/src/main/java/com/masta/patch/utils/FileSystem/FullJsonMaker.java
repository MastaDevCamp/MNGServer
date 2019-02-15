package com.masta.patch.utils.FileSystem;

import com.masta.patch.utils.Compress;
import com.masta.patch.utils.FileSystem.model.DirEntry;
import com.masta.patch.utils.FileSystem.model.FileEntry;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

@Slf4j
@Component
public class FullJsonMaker {

    public static final String compressType = "zip";

    private HashingSystem hashingSystem;

    public FullJsonMaker(final HashingSystem hashingSystem) {
        this.hashingSystem = hashingSystem;
    }

    /**
     * Be called from controller
     *
     * @param path
     * @return Full DirEntry (return all file paths' contents)
     */
    public DirEntry getFileTreeList(String path, String version) {
        DirEntry rootDir = getDirEntry(new File(path), version);
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

        File file = new File(parentDir.getPath());

        for (final File children : file.listFiles()) {
            if (children.isFile()) { //file

                FileEntry childFile = getFileEntry(children, version); //childFile object setting
                parentDir.fileEntryList.add(childFile); //child

            } else if (children.isDirectory()) { //dir

                DirEntry childDir = getDirEntry(children, version);
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

    /**
     * @param file
     * @return DirEntry Filled fields.
     */
    public DirEntry getDirEntry(File file, String version) {
        char fileType = 'D';

        DirEntry dirEntry = DirEntry.builder()
                .type(fileType)
                .path(file.getPath())
                .compress(compressType)
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

        log.info(file.getPath());

        String compressFilePath = Compress.zip(file);
        File compressFile = new File(compressFilePath);

        log.info(compressFile.getPath());

        char fileType = file.getTotalSpace() != 0 ? 'F' : 'G';

        FileEntry fileEntry = FileEntry.builder()
                .type(fileType)
                .path(file.getPath())
                .compress(compressType)
                .originalSize((int) file.length())
                .compressSize((int) compressFile.length())
                .originalHash(hashingSystem.getMD5Hashing(file))
                .compressHash(hashingSystem.getMD5Hashing(compressFile))
                .diffType('x') //patch
                .version(version)
                .build();


        return fileEntry;

    }
}
