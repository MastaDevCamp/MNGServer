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
//                .originalSize((int) file.length())
//                .compressSize((int) file.length())
                .originalHash(hashingSystem.MD5hashing(file))
                .compressHash(hashingSystem.MD5hashing(file))
                .originalHash(getMD5Hash(file))
                .compressHash(getMD5Hash(file))
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

    public String getMD5Hash(File file) {

        if (file.isDirectory()) {
            return "";
        }

        String md5 = "";
        byte[] fileByte = new byte[20];

        try {
            if (file.length() > 20) {
                byte[] last = new byte[10];
                byte[] first = new byte[10];

                RandomAccessFile raf = new RandomAccessFile(file, "r");
                raf.read(first, 0, 10);
                raf.seek(file.length() - 10);
                raf.read(last, 0, 10);

                // combine byte[]
                System.arraycopy(first, 0, fileByte, 0, first.length);
                System.arraycopy(last, 0, fileByte, first.length, last.length);
            } else {
                RandomAccessFile raf = new RandomAccessFile(file, "r");
                raf.read(fileByte, 0, (int) file.length());
            }

            byte[] res = MessageDigest.getInstance("MD5").digest(fileByte);
            md5 = DatatypeConverter.printHexBinary(res);

        } catch (Exception e) {
            log.error(e.getMessage());
        }

        return md5;
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

}
