package com.masta.patch.utils.FileSystem;

import com.masta.patch.utils.FileSystem.model.DirEntry;
import com.masta.patch.utils.FileSystem.model.FileEntry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.ArrayList;

@Slf4j
@Component
public class FullJsonMaker {

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
    public DirEntry getFileTreeList(String path) {
        DirEntry rootDir = getDirEntry(new File(path));
        listFilesForFolder(rootDir);
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
    public void listFilesForFolder(final DirEntry parentDir) {

        parentDir.fileEntryList = new ArrayList<>();
        parentDir.dirEntryList = new ArrayList<>();

        File file = new File(parentDir.getPath());

        for (final File children : file.listFiles()) {
            if (children.isFile()) { //file

                FileEntry childFile = getFileEntry(children); //childFile object setting
                parentDir.fileEntryList.add(childFile); //child

            } else if (children.isDirectory()) { //dir

                DirEntry childDir = getDirEntry(children);
                parentDir.dirEntryList.add(childDir);
                listFilesForFolder(childDir); //자식 dir
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
    public DirEntry getDirEntry(File file) {
        char fileType = 'D';

        DirEntry dirEntry = DirEntry.builder()
                .type(fileType)
                .path(file.getPath())
                .compress("gzip")
                .diffType('x') //patch
                .version("0.1.0") //patch
                .build();

        return dirEntry;
    }

    /**
     * @param file
     * @return FileEntry Filled fields.
     */
    public FileEntry getFileEntry(File file) {

        // outfolder zip

        // set file type
        char fileType = file.getTotalSpace() != 0 ? 'F' : 'G';

        FileEntry fileEntry = FileEntry.builder()
                .type(fileType)
                .path(file.getPath())
                .compress("gzip")
                .originalSize((int) file.length())
                .compressSize((int) file.length())
                .originalHash(hashingSystem.getMD5Hashing(file))
                .compressHash(hashingSystem.getMD5Hashing(file))
                .diffType('x') //patch
                .version("0.1.0")
                .build();

        return fileEntry;
    }

}
