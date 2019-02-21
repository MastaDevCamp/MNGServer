package com.masta.patch.utils.FileSystem;

import com.masta.patch.model.DirEntry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Slf4j
@Component
public class PatchJsonMaker {

    private final int FILE_TYPE = 0;
    private final int FILE_HASHING = 5;
    private final int DIR_DIFF_TYPE = 4;
    private final int FILE_DIFF_TYPE = 8;


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
    public List<String> getPatchFileList(DirEntry beforeJson, DirEntry afterJson) {
        if (beforeJson != null) {
            beforeJsonStrings = typeConverter.jsonToList(typeConverter.makeFileList(beforeJson));
            afterJsonStrings = typeConverter.jsonToList(typeConverter.makeFileList(afterJson));

            try {
                HashMap<String, Integer> beforeHashMap = typeConverter.makePathHashMap(beforeJsonStrings);
                HashMap<String, Integer> afterHashMap = typeConverter.makePathHashMap(afterJsonStrings);

                return compareDiff(beforeHashMap, afterHashMap);

            } catch (Exception e) {
                log.error(e.getMessage());
            }
        }
        return null; // create 만들기
    }


    public List<String> compareDiff(HashMap<String, Integer> before, HashMap<String, Integer> after) {

        List<String> diffStringList = new ArrayList<>();

        log.info("before array");
        log.info(before.toString());
        log.info("after array");
        log.info(after.toString());

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

        for (String path : deleteList) {
            if (beforeJsonStrings.get(before.get(path))[FILE_TYPE].equals("D")) {
                beforeJsonStrings.get(before.get(path))[DIR_DIFF_TYPE] = "D";
                diffStringList.add(typeConverter.arrayToStringFormat(beforeJsonStrings.get(before.get(path)), "D"));
            } else {
                beforeJsonStrings.get(before.get(path))[FILE_DIFF_TYPE] = "D";
                diffStringList.add(typeConverter.arrayToStringFormat(beforeJsonStrings.get(before.get(path)), "F"));
            }
        }
    }


    public void addCreateList(HashMap<String, Integer> before, HashMap<String, Integer> after, List<String> diffStringList) {
        List<String> createList = new ArrayList<>();
        createList.addAll(after.keySet());
        createList.removeAll(before.keySet());

        for (String path : createList) {
            if (afterJsonStrings.get(after.get(path))[FILE_TYPE].equals("D")) {
                afterJsonStrings.get(after.get(path))[DIR_DIFF_TYPE] = "C";
                diffStringList.add(typeConverter.arrayToStringFormat(afterJsonStrings.get(after.get(path)), "D"));
            } else {
                afterJsonStrings.get(after.get(path))[FILE_DIFF_TYPE] = "C";
                diffStringList.add(typeConverter.arrayToStringFormat(afterJsonStrings.get(after.get(path)), "F"));
            }

        }
    }

    public void addUpdateList(HashMap<String, Integer> before, HashMap<String, Integer> after, List<String> diffStringList) {
        List<String> updateList = new ArrayList<>();
        updateList.addAll(before.keySet());
        updateList.retainAll(after.keySet()); //교집합 : 빈 dir 파일이 있으면 file delete로 인해 빈 dir 파일로 create 해주어야 한다.

        for (String path : updateList) {
            if (beforeJsonStrings.get(before.get(path))[FILE_TYPE].equals("F")) {
                if (!beforeJsonStrings.get(before.get(path))[FILE_HASHING].equals(afterJsonStrings.get(after.get(path))[FILE_HASHING])) {
                    beforeJsonStrings.get(before.get(path))[FILE_DIFF_TYPE] = "U";
                    diffStringList.add(typeConverter.arrayToStringFormat(beforeJsonStrings.get(before.get(path)), "F"));
                }
            }

        }
    }


}
