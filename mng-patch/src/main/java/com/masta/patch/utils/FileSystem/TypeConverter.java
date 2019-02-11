package com.masta.patch.utils.FileSystem;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.masta.patch.dto.VersionLog;
import com.masta.patch.mapper.VersionMapper;
import com.masta.patch.utils.FileSystem.model.DirEntry;
import com.masta.patch.utils.FileSystem.model.FileEntry;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;



@Slf4j
@Component
public class TypeConverter
{
    public final static String JSON_EXTENTION = ".json";

    @Value("${PMS.url}")
    private String pmsPath;

    @Value("${file.path}")
    private String mainDir;

    private VersionMapper versionMapper;

    public TypeConverter (final VersionMapper versionMapper){
        this.versionMapper = versionMapper;
    }

    /**
     * convert tree version of full json to string list version
     *
     * @param rootDir
     * @return
     */
    public List<String> makeFileList(DirEntry rootDir){
        List<String> fileList = new ArrayList<>();
        searchFile(rootDir, fileList);

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

    /**
     *
     * convert StringList To Json
     *
     * @param jsonPath
     * @return
     */
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
     *
     * convert jsonString to Array String List
     *
     * @param jsonList
     * @return
     */
    public List<String[]> jsonStringToArray(List<String> jsonList) {

        List<String[]> strings = new ArrayList<>();

        for (String jsonString : jsonList) {
            String stringList[] = jsonString.split(" \\| ");
            strings.add(stringList);
        }

        return strings;
    }


    /**
     *
     * convert Array To String
     *
     * @param strings
     * @param type
     * @return
     */

    public String arrayToStringFormat(String strings[], String type) {
        if (type.equals("D")) {
            System.out.println(String.format("%s | %s | %s | %s | %s ", strings));
            return String.format("%s | %s | %s | %s | %s ", strings);
        } else {
            System.out.println(String.format("%s | %s | %s | %s | %s | %s | %s | %s | %s ", strings));
            return String.format("%s | %s | %s | %s | %s | %s | %s | %s | %s ", strings);
        }

    }

    /**
     *
     * make HashMap
     *
     * @param jsonStringList
     * @return
     */

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

    public DirEntry getRemoteLastVersionJson() {
        DirEntry dirEntry = new DirEntry();
        VersionLog latestVersion = versionMapper.latestVersion(); //local에 저장 후 file읽기 or 그냥 바로 file 읽기

        try{
            File file = new File(latestVersion.getVersion() + JSON_EXTENTION);
            URL url = new URL(pmsPath + latestVersion.getFull());
            URLConnection connection = url.openConnection();
            InputStream is = connection.getInputStream();
            FileUtils.copyInputStreamToFile(is, file);
            dirEntry = readVersionJson(file.getPath());
        }catch (Exception e){
            log.error(e.getMessage());
        }

        return dirEntry;
    }

    public String saveJsonFile(Object object, String fileName) {
        File file = new File(fileName);
        try {
            Gson gson = new Gson();
            String dirJson = gson.toJson(object);
            FileUtils.writeStringToFile(file, dirJson);
        } catch (Exception e) {
            log.error(e.getMessage());
        }

        return file.getPath();
    }
}
