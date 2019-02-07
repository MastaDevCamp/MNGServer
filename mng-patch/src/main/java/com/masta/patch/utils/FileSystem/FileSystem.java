package com.masta.patch.utils.FileSystem;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.masta.patch.utils.FileSystem.model.DirEntry;
import com.masta.patch.utils.FileSystem.model.FileEntry;
import io.swagger.models.auth.In;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.xml.bind.DatatypeConverter;
import java.beans.beancontext.BeanContextSupport;
import java.io.File;
import java.io.RandomAccessFile;
import java.security.MessageDigest;
import java.util.*;


@Slf4j
@Service
public class FileSystem {

    final private HashingSystem hashingSystem;

    private List<String[]> beforeJsonStrings;
    private List<String[]> afterJsonStrings;


    @Value("${file.path}")
    private String mainDir;




    public FileSystem(final HashingSystem hashingSystem, List<String[]> afterJsonStrings, List<String[]> beforeJsonStrings){
        this.hashingSystem = hashingSystem;
        this.afterJsonStrings = afterJsonStrings;
        this.beforeJsonStrings = beforeJsonStrings;
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

        beforeJsonStrings = jsonStringToArray(makeFileList(beforeJson));
        afterJsonStrings = jsonStringToArray(makeFileList(afterJson));


        HashMap<String,Integer> beforeHashMap = makePathHashMap(beforeJsonStrings);
        HashMap<String,Integer> afterHashMap = makePathHashMap(afterJsonStrings);

        return compareDiff(beforeHashMap, afterHashMap);
    }

    public List<String> compareDiff(HashMap<String,Integer> before, HashMap<String,Integer> after){

        List<String> diffStringList = new ArrayList<>();

        //addDeleteList
        addDeleteList(before,after,diffStringList);

        //addCreateList
        addCreateList(before,after,diffStringList);


        //addUpdateList
        addUpdateList(before,after,diffStringList);

        log.info(diffStringList.toString());
        return diffStringList;
    }

    public void addDeleteList(HashMap<String,Integer> before, HashMap<String,Integer> after, List<String> diffStringList){
        List<String> deleteList = new ArrayList<>();
        deleteList.addAll(before.keySet());
        deleteList.removeAll(after.keySet());
        int idx;
        for(String path : deleteList) {
            char type;
            if (beforeJsonStrings.get(before.get(path))[0].equals("D")) {
                if(dirMostCheck(path, before)){
                    continue;
                }
                type = 'D'; idx = 4;
            }else{
                type = 'F'; idx = 8;
            }
            beforeJsonStrings.get(before.get(path))[idx] = "D";
            diffStringList.add(arrayToStringFormat(beforeJsonStrings.get(before.get(path)), type));
        }
    }

    public boolean dirMostCheck(String path, HashMap<String,Integer> before){
        for(String childPath : before.keySet()){
            if(childPath.contains(path)){
                return true; //삭제하면 안되는 dir
            }
        }
        return false;

    }

    public void addCreateList(HashMap<String,Integer> before, HashMap<String,Integer> after, List<String> diffStringList){
        List<String> createList = new ArrayList<>();
        createList.addAll(after.keySet());
        createList.removeAll(before.keySet());

        int idx ;
        for(String path : createList){
            char type;
            if (afterJsonStrings.get(after.get(path))[0].equals("D")) {
                type = 'D'; idx = 4;
            }else{
                type = 'F'; idx = 8;
            }
                afterJsonStrings.get(after.get(path))[idx] = "C";
                diffStringList.add(arrayToStringFormat(afterJsonStrings.get(after.get(path)), type));
        }
    }

    public void addUpdateList(HashMap<String,Integer> before, HashMap<String,Integer> after, List<String> diffStringList){
        List<String> updateList = new ArrayList<>();
        updateList.addAll(before.keySet());
        updateList.retainAll(after.keySet()); //교집합

        for(String path : updateList){
            if(beforeJsonStrings.get(before.get(path))[0].equals("F")){
                if(!beforeJsonStrings.get(before.get(path))[5].equals(afterJsonStrings.get(after.get(path))[5])) {
                    beforeJsonStrings.get(before.get(path))[8] = "U";
                    diffStringList.add(arrayToStringFormat(beforeJsonStrings.get(before.get(path)), 'F'));
                }
            }
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


    public String arrayToStringFormat(String strings[], char type){
        if(type == 'D'){
            System.out.println(String.format("%s | %s | %s | %s | %s ", strings));
            return String.format("%s | %s | %s | %s | %s ", strings);
        }else{
            System.out.println(String.format("%s | %s | %s | %s | %s | %s | %s | %s | %s ", strings));
            return String.format("%s | %s | %s | %s | %s | %s | %s | %s | %s ", strings);
        }

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