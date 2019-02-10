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
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.attribute.FileTime;
import java.security.MessageDigest;
import java.util.*;


@Slf4j
@Service
public class FileSystem {

    final private HashingSystem hashingSystem;

    private List<String[]> beforeJsonStrings;
    private List<String[]> afterJsonStrings;


    @Value("${patchJson.file.path}")
    private String patchDir;

    @Value("${file.path}")
    private String mainDir;


    public FileSystem(final HashingSystem hashingSystem, List<String[]> afterJsonStrings, List<String[]> beforeJsonStrings) {
        this.hashingSystem = hashingSystem;
        this.afterJsonStrings = afterJsonStrings;
        this.beforeJsonStrings = beforeJsonStrings;
    }


    /**
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

            } else if (children.isDirectory()) { //dir

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
     * convert object to string list
     *
     * @param rootDir
     * @param fileList
     */
    public void searchFile(DirEntry rootDir, List<String> fileList) {
        for (FileEntry fileEntry : rootDir.fileEntryList) {
            fileList.add(fileEntry.print());
        }

        if (rootDir.dirEntryList.size() == 0) {
            fileList.add(rootDir.print());
        } else {
            for (DirEntry dirEntry : rootDir.dirEntryList) {
                searchFile(dirEntry, fileList);
            }
        }
    }

    /**
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

    public List<String> readStringListToJson(String jsonPath) {
        List<String> strings = new ArrayList<>();
        ObjectMapper mapper = new ObjectMapper();
        try {
            strings = mapper.readValue(new File(jsonPath), List.class);
        } catch (IOException e) {
            log.error(e.getMessage());
            e.printStackTrace();
        }
        return strings;
    }

    /**
     * make full json to patch json's string list
     *
     * @param beforeJson
     * @param afterJson
     * @return
     */
    public List<String> getPatchJson(String beforeJson, String afterJson) {

        beforeJsonStrings = jsonStringToArray(makeFileList(beforeJson));
        afterJsonStrings = jsonStringToArray(makeFileList(afterJson));

        HashMap<String, Integer> beforeHashMap = makePathHashMap(beforeJsonStrings);
        HashMap<String, Integer> afterHashMap = makePathHashMap(afterJsonStrings);

        return compareDiff(beforeHashMap, afterHashMap);
    }

    public List<String> compareDiff(HashMap<String, Integer> before, HashMap<String, Integer> after) {

        List<String> diffStringList = new ArrayList<>();

        //addDeleteList
        addDeleteList(before, after, diffStringList);

        //addCreateList
        addCreateList(before, after, diffStringList);


        //addUpdateList
        addUpdateList(before, after, diffStringList);

        log.info(diffStringList.toString());
        return diffStringList;
    }

    public void addDeleteList(HashMap<String, Integer> before, HashMap<String, Integer> after, List<String> diffStringList) {
        List<String> deleteList = new ArrayList<>();
        deleteList.addAll(before.keySet());
        deleteList.removeAll(after.keySet());
        int idx;
        for (String path : deleteList) {
            String type;
            if (beforeJsonStrings.get(before.get(path))[0].equals("D")) {
                if (dirMostCheck(path, before)) {
                    continue;
                }
                type = "D";
                idx = 4;
            } else {
                type = "F";
                idx = 8;
            }
            beforeJsonStrings.get(before.get(path))[idx] = "D";
            diffStringList.add(arrayToStringFormat(beforeJsonStrings.get(before.get(path)), type));
        }
    }

    public boolean dirMostCheck(String path, HashMap<String, Integer> before) {
        for (String childPath : before.keySet()) {
            if (childPath.contains(path)) {
                return true; //삭제하면 안되는 dir
            }
        }
        return false;

    }

    public void addCreateList(HashMap<String, Integer> before, HashMap<String, Integer> after, List<String> diffStringList) {
        List<String> createList = new ArrayList<>();
        createList.addAll(after.keySet());
        createList.removeAll(before.keySet());

        int idx;
        for (String path : createList) {
            String type;
            if (afterJsonStrings.get(after.get(path))[0].equals("D")) {
                type = "D";
                idx = 4;
            } else {
                type = "F";
                idx = 8;
            }
            afterJsonStrings.get(after.get(path))[idx] = "C";
            diffStringList.add(arrayToStringFormat(afterJsonStrings.get(after.get(path)), type));
        }
    }

    public void addUpdateList(HashMap<String, Integer> before, HashMap<String, Integer> after, List<String> diffStringList) {
        List<String> updateList = new ArrayList<>();
        updateList.addAll(before.keySet());
        updateList.retainAll(after.keySet()); //교집합

        for (String path : updateList) {
            if (beforeJsonStrings.get(before.get(path))[0].equals("F")) {
                if (!beforeJsonStrings.get(before.get(path))[5].equals(afterJsonStrings.get(after.get(path))[5])) {
                    beforeJsonStrings.get(before.get(path))[8] = "U";
                    diffStringList.add(arrayToStringFormat(beforeJsonStrings.get(before.get(path)), "F"));
                }
            }
        }
    }


    public String getDiffType(String[] fileString) {
        if (fileString[0].equals("F")) {
            return fileString[8];
        }
        return fileString[4];
    }

    public void setDiffType(String[] fileString, String type) {
        if (fileString[0].equals("F")) {
            fileString[8] = type;
        }
        fileString[4] = type;
    }

    public List<String[]> jsonStringToArray(List<String> jsonList) {

        List<String[]> strings = new ArrayList<>();

        for (String jsonString : jsonList) {
            String stringList[] = jsonString.split(" \\| ");
            strings.add(stringList);
        }

        return strings;
    }


    public String arrayToStringFormat(String strings[], String type) {
        if (type.equals("D")) {
            System.out.println(String.format("%s | %s | %s | %s | %s ", strings));
            return String.format("%s | %s | %s | %s | %s ", strings);
        } else {
            System.out.println(String.format("%s | %s | %s | %s | %s | %s | %s | %s | %s ", strings));
            return String.format("%s | %s | %s | %s | %s | %s | %s | %s | %s ", strings);
        }

    }

    public HashMap<String, Integer> makePathHashMap(List<String[]> jsonStringList) {

        HashMap<String, Integer> hashMap = new HashMap<>();

        int dirIndex = mainDir.length();

        int i = 0;
        for (String[] jsonString : jsonStringList) {
            String pathString = jsonString[1].substring(dirIndex);
            hashMap.put(pathString, i++);
        }
        return hashMap;

    }

    /**
     * make merge json
     */

    public static List<String> mergeJsonList;
    public static List<String[]> intermediateList;
    public static HashMap<String, Integer> intermediateHashMap;
    public static List<String[]> diffArrayList;

    public List<String> makeMergeJson(String before, String after) {
        mergeJsonList = new ArrayList<>();
        intermediateList = new ArrayList<>();
        intermediateHashMap = new HashMap<>();

        log.info("before version : " + before);
        log.info("after version : " + after);
        int start = convertVer(before);
        int end = convertVer(after);
        List<File> files = patchJsonList(start, end);
        for (File file : files) {
            intermediateHashMap = makePathHashMap(intermediateList);
            fileRead(file);
        }

        //arrayToFormat
        for (String[] mergeList : intermediateList) {
            mergeJsonList.add(arrayToStringFormat(mergeList, mergeList[0]));
        }
        return mergeJsonList;
    }

    public List<File> patchJsonList(int start, int end) {
        log.info("patchJsonList");
        File dir = new File(patchDir);
        List<File> patchJsonFiles = new ArrayList<>();

        File[] files = dir.listFiles();
        for (File file : files) {
            int pos = file.getName().lastIndexOf(".");
            if (pos != -1) {
                String name = file.getName().substring(0, pos);
                String[] version = name.split("v");
                int fileStart = convertVer(version[1]);
                int fileEnd = convertVer(version[2]);
                if (start <= fileStart && end >= fileEnd) { //범위 안에 들면
                    patchJsonFiles.add(file);
                }
            }
        }
        log.info(files.toString());
        return patchJsonFiles;
    }

    public void checkDiff(String path, int index) {
        if (intermediateHashMap.containsKey(path)) {
            diffTypeChange(path, index);
        } else {
            intermediateList.add(diffArrayList.get(index));
        }
    }

    public void fileRead(File file) {
        diffArrayList = jsonStringToArray(readStringListToJson(file.getPath()));
        HashMap<String, Integer> pathNowHashMap = makePathHashMap(diffArrayList);
        for (String nowPath : pathNowHashMap.keySet()) {
            checkDiff(nowPath, pathNowHashMap.get(nowPath));
        }
    }

    public void diffTypeChange(String path, int index) {
        int interIdx = intermediateHashMap.get(path);
        String[] interString = intermediateList.get(interIdx);
        String beforeType = getDiffType(interString);

        String[] nowString = diffArrayList.get(index);
        String nowType = getDiffType(nowString);

        String fileType = nowString[0]; // fileType = F or D

        /**
         * Rule
         *  BeforeType | AfterType | chagneType
         *  C   U   C
         *  C   D   X
         *  U   U   U
         *  U   D   D
         *  D   C   U
         */

        if (fileType.equals("D")) {
            if (beforeType.equals("D") && nowType.equals("C") || beforeType.equals("C") && nowType.equals("D")) {
                intermediateList.remove(interIdx);
            }
        } else { //F
            switch (beforeType) {
                case "C":
                    switch (nowType) {
                        case "U":
                            setDiffType(nowString, "C");
                            intermediateList.set(interIdx, nowString);
                            break;
                        case "D":
                            intermediateList.remove(interIdx);
                            break;
                    }
                    break;
                case "U":
                    switch (nowType) {
                        case "U":
                        case "D":
                            setDiffType(nowString, nowType);
                            intermediateList.set(interIdx, nowString);
                            break;
                    }
                case "D":
                    switch (nowType) {
                        case "C":
                            setDiffType(nowString, "U");
                            intermediateList.set(interIdx, nowString);
                            break;
                    }
            }
        }

    }


    /**
     * convert version format 0.0.1 to 000001
     */
    public int convertVer(String version) {
        String[] strVer = version.split("\\.");
        int intVer = 0;
        for (int i = 0; i < strVer.length; i++) {
            int digit = Integer.parseInt(strVer[strVer.length - 1 - i]);
            intVer += digit * Math.pow(10, 2 * i);
        }
        return intVer;
    }

    public static void main(String args[]) {
        String jsonPath = "C:\\Users\\user\\Desktop\\smilegate\\patch_test\\patch\\v0.0.1v0.0.2.json";
        List<String> strings = new ArrayList<>();
        ObjectMapper mapper = new ObjectMapper();
        try {
            strings = mapper.readValue(new File(jsonPath), List.class);
        } catch (IOException e) {
            log.error(e.getMessage());
            e.printStackTrace();
        }
        System.out.println(strings);
        for (String file : strings) {
            System.out.println(file);
        }
        return;

    }
}