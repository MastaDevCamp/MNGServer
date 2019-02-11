package com.masta.patch.utils.FileSystem;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Component
@Slf4j
public class MergeJsonMaker {

    @Value("${patchJson.file.path}")
    private String patchDir;

    private TypeConverter typeConverter;

    public MergeJsonMaker(final TypeConverter typeConverter){
        this.typeConverter = typeConverter;
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
        int start = typeConverter.convertVer(before);
        int end = typeConverter.convertVer(after);
        List<File> files = patchJsonList(start, end);
        for (File file : files) {
            intermediateHashMap = typeConverter.makePathHashMap(intermediateList);
            fileRead(file);
        }

        //arrayToFormat
        for (String[] mergeList : intermediateList) {
            mergeJsonList.add(typeConverter.arrayToStringFormat(mergeList, mergeList[0]));
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
                int fileStart = typeConverter.convertVer(version[1]);
                int fileEnd = typeConverter.convertVer(version[2]);
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
        diffArrayList = typeConverter.jsonStringToArray(typeConverter.readStringListToJson(file.getPath()));
        HashMap<String, Integer> pathNowHashMap = typeConverter.makePathHashMap(diffArrayList);
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
     * 타입 지정자도 만들기
     * @param fileString
     * @return
     */
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

}
