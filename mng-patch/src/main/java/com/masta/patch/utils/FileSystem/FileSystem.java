package com.masta.patch.utils.FileSystem;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.masta.patch.utils.FileSystem.model.DirEntry;
import com.masta.patch.utils.FileSystem.model.FileEntry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.xml.bind.DatatypeConverter;
import java.io.File;
import java.io.RandomAccessFile;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Slf4j
@Service
public class FileSystem {

    final private HashingSystem hashingSystem;

    public FileSystem(final HashingSystem hashingSystem){
        this.hashingSystem = hashingSystem;
    }

    public void listFilesForFolder(final DirEntry parentDir) {

        parentDir.fileEntryList = new ArrayList<>();
        parentDir.dirEntryList = new ArrayList<>();

        File file = new File(parentDir.getPath());

        for (final File children : file.listFiles()) {
            if (children.isFile()) { //file

                FileEntry childFile = getFileEntry(children); //childFile obejct setting
                parentDir.fileEntryList.add(childFile); //child

            }else if (children.isDirectory()){ //dir


                DirEntry childDir = getDirEntry(children);
                parentDir.dirEntryList.add(childDir);
                listFilesForFolder(childDir); //자식 dir
            }
        }
    }

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
     * @return FileEntrey Filled fields.
     */
    public FileEntry getFileEntry(File file) {

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

    public void jsonToPOJO(String path) {
        ObjectMapper mapper = new ObjectMapper();

        try {
            FileEntry[] fileEntries = mapper.readValue(new File(path), FileEntry[].class);

            for (FileEntry fileEntry : fileEntries) {
                log.info(fileEntry.toString());
            }

        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }


    public DirEntry getFileTreeList(String path) {
        long startTime = System.currentTimeMillis();
        DirEntry rootDir = getDirEntry(new File(path));
        listFilesForFolder(rootDir);
        log.info("version " + rootDir.getVersion() + " created.");
        long endTime = System.currentTimeMillis();
        System.out.println("That took " + (endTime - startTime) + " milliseconds");
        return rootDir;
    }

    public List<String> makeFileList(String jsonPath) {
        List<String> fileList = new ArrayList<>();
        DirEntry rootDir = readVersionJson(jsonPath);

        searchFile(rootDir, fileList);
        Collections.sort(fileList);

        return fileList;
    }

    public void searchFile(DirEntry rootDir, List<String> fileList) {
        for(FileEntry fileEntry : rootDir.fileEntryList) {
            fileList.add(fileEntry.print());
        }

        if( rootDir.dirEntryList.size() == 0) {
            fileList.add(rootDir.print());
        } else {
            for(DirEntry dirEntry : rootDir.dirEntryList) {
                searchFile(dirEntry, fileList);
            }
        }
    }


    public DirEntry readVersionJson(String jsonPath) {
        DirEntry dirEntry = new DirEntry();

        ObjectMapper mapper = new ObjectMapper();
        try {
            dirEntry = mapper.readValue(new File(jsonPath), DirEntry.class);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return dirEntry;
    }
}