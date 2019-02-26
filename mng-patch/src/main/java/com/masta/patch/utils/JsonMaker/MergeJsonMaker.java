package com.masta.patch.utils.JsonMaker;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.masta.core.response.DefaultRes;
import com.masta.core.response.ResponseMessage;
import com.masta.core.response.StatusCode;
import com.masta.patch.dto.VersionLog;
import com.masta.patch.model.JsonType;
import com.masta.patch.utils.FileMove.LocalFileReadWrite;
import com.masta.patch.utils.HttpConnection;
import com.masta.patch.utils.TypeConverter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import static com.masta.patch.utils.TypeConverter.arrayToStringFormat;
import static com.masta.patch.utils.TypeConverter.jsonToList;

@Component
@Slf4j
public class MergeJsonMaker {

    private final int FILE_TYPE = 0;
    private final int DIR_DIFF_TYPE = 4;
    private final int FILE_DIFF_TYPE = 8;
    private final int PATH = 1;

    @Value("${local.path}")
    private String localPath;


    @Value("${nginx.url}")
    private String nginXPath;


    @Autowired
    private LocalFileReadWrite localFileReadWrite;
    @Autowired
    private TypeConverter typeConverter;
    @Autowired
    private HttpConnection httpConnection;

    /**
     * make merge json
     */

    public static HashMap<String, String[]> intermediateHashMap;


    //new
    public List<String> makeMergeJson_HTTP(List<VersionLog> patchJsonPathList){

        List<String> mergeJsonList = new ArrayList<>();
        intermediateHashMap = new HashMap<>();

        for(VersionLog jsonPath : patchJsonPathList){
            try {
                List<String> patchJsonContent = getPatchJsonContent(jsonPath.getPatch());
                jsonRead(patchJsonContent);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        for (String[] mergeList : intermediateHashMap.values()) {
            mergeJsonList.add(arrayToStringFormat(mergeList, mergeList[0]));
        }
        return mergeJsonList;
    }

    //new
    public void jsonRead(List<String> patchJsonFile){
        List<String[]> diffArrayList = jsonToList(patchJsonFile);

        HashMap<String, String[]> pathNowHashMap = typeConverter.makePathHashMap(diffArrayList);

        for (String nowPath : pathNowHashMap.keySet()) {
            checkDiff(nowPath, pathNowHashMap.get(nowPath));
        }
    }

    //new
    public List<String> getPatchJsonContent(final String inputUrl) throws Exception{
        String urlPath = nginXPath + inputUrl; //properties 속성으로 바꾸기

        String jsonContent = httpConnection.readResponse(urlPath);

        ObjectMapper objectMapper = new ObjectMapper();

        List<String> stringList = objectMapper.readValue(jsonContent, List.class);
        return stringList;
    }

    public List<String> makeMergeJson() {
        List<String> mergeJsonList = new ArrayList<>();
        intermediateHashMap = new HashMap<>();

        List<File> files = patchJsonList();
        log.info(files.toString());

        for (File file : files) {
            fileRead(file);
        }

        //arrayToFormat
        for (String[] mergeList : intermediateHashMap.values()) {
            mergeJsonList.add(arrayToStringFormat(mergeList, mergeList[0]));
        }
        return mergeJsonList;
    }

    public List<File> patchJsonList() {
        log.info("patchJsonList");
        File dir = new File(localPath + "merge");
        List<File> patchJsonFiles = new ArrayList<>();

        File[] files = dir.listFiles();
        for (File file : files) {
            patchJsonFiles.add(file);
        }
        return patchJsonFiles;
    }

    public void fileRead(File file) {

        List<String[]> diffArrayList = jsonToList(localFileReadWrite.patchJsonToFileList(file.getPath()));

        HashMap<String, String[]> pathNowHashMap = typeConverter.makePathHashMap(diffArrayList);

        for (String nowPath : pathNowHashMap.keySet()) {
            checkDiff(nowPath, pathNowHashMap.get(nowPath));
        }
    }

    public void checkDiff(String path, String[] fileInfo) {
        if (intermediateHashMap.containsKey(path)) {
            diffTypeChange(path, fileInfo);
        } else {
            intermediateHashMap.put(path, fileInfo);
        }
    }

    public void diffTypeChange(String path, String[] fileInfo) {
        String[] interFileString = intermediateHashMap.get(path);
        String beforeType = getDiffType(interFileString);

        String nowType = getDiffType(fileInfo);

        String fileType = fileInfo[FILE_TYPE]; // fileType = F or D

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
                intermediateHashMap.remove(path);
            }
        } else { //F
            switch (beforeType) {
                case "C":
                    switch (nowType) {
                        case "U":
                            setDiffType(fileInfo, "C");
                            intermediateHashMap.put(path, fileInfo);
                            break;
                        case "D":
                            intermediateHashMap.remove(path);
                            break;
                    }
                    break;
                case "U":
                    switch (nowType) {
                        case "U":
                        case "D":
                            setDiffType(fileInfo, nowType);
                            intermediateHashMap.put(path, fileInfo);
                            break;
                    }
                case "D":
                    switch (nowType) {
                        case "C":
                            setDiffType(fileInfo, "U");
                            intermediateHashMap.put(path, fileInfo);
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
        } else {
            fileString[DIR_DIFF_TYPE] = type;
        }
    }
}
