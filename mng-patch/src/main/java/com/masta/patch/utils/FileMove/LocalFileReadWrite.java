package com.masta.patch.utils.FileMove;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.masta.patch.dto.VersionLog;
import com.masta.patch.model.DirEntry;
import com.masta.patch.model.JsonType;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.masta.patch.utils.TypeConverter.makeFileList;


@Component
@Slf4j
public class LocalFileReadWrite {

    @Value("${local.path}")
    public String localPath;

    private final NginXFileRead nginXFileRead;

    public LocalFileReadWrite(NginXFileRead nginXFileRead){
        this.nginXFileRead = nginXFileRead;
    }


    public File saveLocal(MultipartFile sourceFile) {
        resetDir(localPath);
        File file = new File(localPath + sourceFile.getName() + ".zip");
        try {
            sourceFile.transferTo(file);
            log.info("save file [" + file.getPath() + "]");
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return file;
    }

    public static void resetDir(String path) {
        try {
            FileUtils.deleteDirectory(new File(path));
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        new File(path).mkdirs();
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

    public DirEntry getRemoteJsonToObject(VersionLog latestVersion){
        File jsonPath = nginXFileRead.getRemoteVersionJson(latestVersion, JsonType.FULL);
        return fullJsonToFileTree(jsonPath.getPath());
    }

}