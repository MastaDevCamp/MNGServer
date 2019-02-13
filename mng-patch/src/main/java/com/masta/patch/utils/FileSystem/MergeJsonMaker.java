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

    private final int FILE_TYPE = 0;
    private final int DIR_DIFF_TYPE = 4;
    private final int FILE_DIFF_TYPE = 8;

    @Value("${local.merge.path}")
    private String patchDir;

    private TypeConverter typeConverter;

    public MergeJsonMaker(final TypeConverter typeConverter) {
        this.typeConverter = typeConverter;
    }

    /**
     * make merge json
     */

    public static List<String> mergeJsonList;
    public static List<String[]> intermediateList;
    public static HashMap<String, Integer> intermediateHashMap;
    public static List<String[]> diffArrayList;

    public List<String> makeMergeJson() {
        mergeJsonList = new ArrayList<>();
        intermediateList = new ArrayList<>();
        intermediateHashMap = new HashMap<>();

        List<File> files = patchJsonList();

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

    public List<File> patchJsonList() {
        log.info("patchJsonList");
        File dir = new File(patchDir);
        List<File> patchJsonFiles = new ArrayList<>();

        File[] files = dir.listFiles();
        for (File file : files) {
            patchJsonFiles.add(file);
        }
        return patchJsonFiles;
    }


    public void fileRead(File file) {
        diffArrayList = typeConverter.jsonStringToArray(typeConverter.readStringListToJson(file.getPath()));
        HashMap<String, Integer> pathNowHashMap = typeConverter.makePathHashMap(diffArrayList);
        for (String nowPath : pathNowHashMap.keySet()) {
            checkDiff(nowPath, pathNowHashMap.get(nowPath));
        }
    }

    public void checkDiff(String path, int index) {
        if (intermediateHashMap.containsKey(path)) {
            diffTypeChange(path, index);
        } else {
            intermediateList.add(diffArrayList.get(index));
        }
    }


    public void diffTypeChange(String path, int index) {
        int interIdx = intermediateHashMap.get(path);
        String[] interString = intermediateList.get(interIdx);
        String beforeType = getDiffType(interString);

        String[] nowString = diffArrayList.get(index);
        String nowType = getDiffType(nowString);

        String fileType = nowString[FILE_TYPE]; // fileType = F or D

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
     *
     * @param fileString
     * @return
     */
    public String getDiffType(String[] fileString) {
        if (fileString[FILE_TYPE].equals("F")) {
            return fileString[FILE_DIFF_TYPE];
        }
        return fileString[DIR_DIFF_TYPE];
    }

    public void setDiffType(String[] fileString, String type) {

        if (fileString[FILE_TYPE].equals("F")) {
            fileString[FILE_DIFF_TYPE] = type;
        }else{
            fileString[DIR_DIFF_TYPE] = type;
        }
    }
}
