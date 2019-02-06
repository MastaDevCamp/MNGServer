package com.masta.patch.utils.FileSystem;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.masta.patch.utils.FileSystem.model.DirEntry;
import com.masta.patch.utils.FileSystem.model.FileEntry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.xml.bind.DatatypeConverter;
import java.io.File;
import java.io.RandomAccessFile;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;



@Slf4j
@Service
public class FileSystem {

    final private HashingSystem hashingSystem;


    @Value("${file.path}")
    private String mainDir;


    public FileSystem(final HashingSystem hashingSystem){
        this.hashingSystem = hashingSystem;
    }


    /**
     *
     * Be called from controller
     *
     * @param path
     * @return Full DirEntry (return all file paths' contents)
     */
    public DirEntry getFileTreeList(String path) {
//        long startTime = System.currentTimeMillis(); //time check
        DirEntry rootDir = getDirEntry(new File(path));
        listFilesForFolder(rootDir);
        log.info("version " + rootDir.getVersion() + " created.");
//        long endTime = System.currentTimeMillis();
//        System.out.println("That took " + (endTime - startTime) + " milliseconds");
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

            }else if (children.isDirectory()){ //dir

                DirEntry childDir = getDirEntry(children);
                parentDir.dirEntryList.add(childDir);
                listFilesForFolder(childDir); //자식 dir
            }
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


    /**
     * convert tree version of full json to string list version
     *
     * @param jsonPath
     * @return
     */
    public List<String> makeFileList(String jsonPath) {
        List<String> fileList = new ArrayList<>();
        DirEntry rootDir = readVersionJson(jsonPath);

        searchFile(rootDir, fileList);
        Collections.sort(fileList);

        return fileList;
    }


    /**
     *
     * convert object to string list
     *
     * @param rootDir
     * @param fileList
     */
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

    /**
     *
     * convert json to pojo type
     *
     * @param jsonPath
     * @return DirEntry
     */
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


    /**
     *
     *
     * make full json to patch json's string list
     *
     * @param beforeJson
     * @param afterJson
     * @return
     */
    public List<String> getPatchJson(String beforeJson, String afterJson){


        List<String[]> beforeJsonStrings = jsonStringToArray(makeFileList(beforeJson));
        List<String[]> afterJsonStrings = jsonStringToArray(makeFileList(afterJson));

        HashMap<String,Integer> beforeHashMap = makePathHashMap(beforeJsonStrings);
        HashMap<String,Integer> afterHashMap = makePathHashMap(afterJsonStrings);

        return null;
    }

    public void compareDiff(HashMap<String,Integer> before, HashMap<String,Integer> after){
        for(String path: before.keySet()){

        }
    }

    public List<String[]> jsonStringToArray (List<String> jsonList){

        List<String[]> strings = new ArrayList<>();

        for(String jsonString: jsonList){
            String stringList[] = jsonString.split(" \\| ");
            strings.add(stringList);
        }

        return strings;
    }


    public HashMap<String, Integer> makePathHashMap(List<String[]> jsonStringList){

        HashMap<String, Integer> hashMap = new HashMap<>();

        int dirIndex = mainDir.length();

        int i=0;
        for(String[] jsonString : jsonStringList){
            String pathString = jsonString[1].substring(dirIndex);
            hashMap.put(pathString, i++);
        }
        return hashMap;

    }


}