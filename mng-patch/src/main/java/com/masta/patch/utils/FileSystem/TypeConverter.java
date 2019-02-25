package com.masta.patch.utils.FileSystem;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.masta.patch.dto.VersionLog;
import com.masta.patch.mapper.VersionMapper;
import com.masta.patch.model.DirEntry;
import com.masta.patch.model.FileEntry;
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
public class TypeConverter {
    public final static String JSON_EXTENTION = ".json";

    @Value("${nginx.url}")
    private String nginXPath;


    @Value("${local.path}")
    private String localPath;

    @Value("${file.path}")
    private String mainDir;

    private VersionMapper versionMapper;

    public TypeConverter(final VersionMapper versionMapper) {
        this.versionMapper = versionMapper;
    }

    /**
     * convert tree version of full json to string list version
     *
     * @param rootDir
     * @return
     */
    public List<String> makeFileList(DirEntry rootDir) {
        if (rootDir == null)
            return null;

        List<String> fileList = new ArrayList<>();

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
        if (!rootDir.getPath().equals("")) {
            fileList.add(rootDir.print());
        }
        for (DirEntry dirEntry : rootDir.dirEntryList) {
            searchFile(dirEntry, fileList);
        }
    }

    /**
     * convert json to pojo type
     *
     * @param jsonPath
     * @return DirEntry
     */
    public DirEntry fullJsonToFileTree(String jsonPath) {
        DirEntry dirEntry = new DirEntry();

        ObjectMapper mapper = new ObjectMapper();
        try {
            dirEntry = mapper.readValue(new File(jsonPath), DirEntry.class);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return dirEntry;
    }

    public List<String> fullJsonToFileList(String jsonPath) {
        DirEntry fullFileTree = fullJsonToFileTree(jsonPath);
        return makeFileList(fullFileTree);
    }

    /**
     * convert StringList To Json
     *
     * @param jsonPath
     * @return
     */
    public List<String> patchJsonToFileList(String jsonPath) {
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
     * convert jsonString to Array String List
     *
     * @param jsonList
     * @return
     */
    public List<String[]> jsonToList(List<String> jsonList) {

        if (jsonList == null)
            return null;

        List<String[]> strings = new ArrayList<>();

        for (String jsonString : jsonList) {
            String stringList[] = jsonString.split(" \\| ");
            stringList[stringList.length - 1] = stringList[stringList.length - 1].trim();
            strings.add(stringList);
        }

        return strings;
    }


    /**
     * convert Array To String
     *
     * @param strings
     * @param type
     * @return
     */

    public String arrayToStringFormat(String strings[], String type) {
        if (type.equals("D")) {
            return String.format("%s | %s | %s | %s | %s", strings);
        } else {
            return String.format("%s | %s | %s | %s | %s | %s | %s | %s | %s", strings);
        }

    }

    /**
     * make HashMap
     *
     * @param jsonStringList
     * @return
     */

    public HashMap<String, Integer> makePathHashMap(List<String[]> jsonStringList) {
        if (jsonStringList == null)
            return new HashMap<String, Integer>();

        HashMap<String, Integer> hashMap = new HashMap<>();

        int i = 0;
        for (String[] jsonString : jsonStringList) {
            String pathString = jsonString[1];
            hashMap.put(pathString, i++);
        }
        return hashMap;

    }

    public DirEntry getRemoteLatestVersionJson() {
        DirEntry dirEntry = null;
        VersionLog latestVersion = versionMapper.latestVersion(); //local에 저장 후 file읽기 or 그냥 바로 file 읽기
        if (latestVersion != null) {
            try {
                File file = new File(localPath + String.format("Full_Ver_%s.json", latestVersion.getVersion()));
                URL url = new URL(nginXPath + latestVersion.getFull());
                URLConnection connection = url.openConnection();
                InputStream is = connection.getInputStream();
                FileUtils.copyInputStreamToFile(is, file);
                dirEntry = fullJsonToFileTree(file.getPath())


                ;
            } catch (Exception e) {
                log.error(e.getMessage());
            }
        }
        return dirEntry;
    }

    public File saveJsonFile(Object obj, String path) {
        File file = new File(localPath + path);
        try {
            Gson gson = new Gson();
            String dirJson = gson.toJson(obj);
            FileUtils.writeStringToFile(file, dirJson);
        } catch (Exception e) {
            log.error(e.getMessage());
        }

        return file;
    }

}
