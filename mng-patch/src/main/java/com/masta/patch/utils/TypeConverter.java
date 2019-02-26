package com.masta.patch.utils;

import com.masta.patch.model.DirEntry;
import com.masta.patch.model.FileEntry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;


@Slf4j
@Component
public class TypeConverter {

    @Value("${file.path}")
    private String mainDir;

    private static final int PATH = 1;

    /**
     * convert tree version of full json to string list version
     *
     * @param rootDir
     * @return
     */
    public static List<String> makeFileList(DirEntry rootDir) {
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
    public static void searchFile(DirEntry rootDir, List<String> fileList) {
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
     * convert jsonString to Array String List
     *
     * @param jsonList
     * @return
     */
    public static List<String[]> jsonToList(List<String> jsonList) {

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

    public static String arrayToStringFormat(String strings[], String type) {
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

    public HashMap<String, String[]> makePathHashMap(List<String[]> jsonStringList) {
        if (jsonStringList == null)
            return new HashMap<String, String[]>();

        HashMap<String, String[]> hashMap = new HashMap<>();

        for (String[] nowPath : jsonStringList) {
            hashMap.put(nowPath[PATH], nowPath);
        }
        return hashMap;
    }


}
