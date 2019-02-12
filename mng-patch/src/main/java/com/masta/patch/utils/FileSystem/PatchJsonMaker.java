package com.masta.patch.utils.FileSystem;

import com.masta.patch.utils.FileSystem.model.DirEntry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Slf4j
@Component
public class PatchJsonMaker {

    private List<String[]> beforeJsonStrings;
    private List<String[]> afterJsonStrings;

    private TypeConverter typeConverter;

    public PatchJsonMaker(final TypeConverter typeConverter) {
        this.typeConverter = typeConverter;
    }

    /**
     * make full json to patch json's string list
     *
     * @param beforeJson
     * @param afterJson
     * @return
     */
    public List<String> getPatchJson(DirEntry beforeJson, DirEntry afterJson) {
        if (beforeJson != null) {
            beforeJsonStrings = typeConverter.jsonStringToArray(typeConverter.makeFileList(beforeJson));
            afterJsonStrings = typeConverter.jsonStringToArray(typeConverter.makeFileList(afterJson));

            try {
                HashMap<String, Integer> beforeHashMap = typeConverter.makePathHashMap(beforeJsonStrings);
                HashMap<String, Integer> afterHashMap = typeConverter.makePathHashMap(afterJsonStrings);

                return compareDiff(beforeHashMap, afterHashMap);

            } catch (Exception e) {
                return null;
            }

        }

        return null; // create 만들기
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
            diffStringList.add(typeConverter.arrayToStringFormat(beforeJsonStrings.get(before.get(path)), type));
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
            diffStringList.add(typeConverter.arrayToStringFormat(afterJsonStrings.get(after.get(path)), type));
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
                    diffStringList.add(typeConverter.arrayToStringFormat(beforeJsonStrings.get(before.get(path)), "F"));
                }
            }
        }
    }


}
