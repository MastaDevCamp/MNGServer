package com.masta.patch.utils.JsonMaker;

import com.masta.patch.model.DirEntry;
import io.swagger.models.auth.In;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.masta.patch.utils.TypeConverter.*;

@Slf4j
@Component
public class PatchJsonMaker {

    private final int FILE_TYPE = 0;
    private final int FILE_HASHING = 5;
    private final int DIR_DIFF_TYPE = 4;
    private final int FILE_DIFF_TYPE = 8;

    /**
     * make full json to patch json's string list
     *
     * @param beforeJson
     * @param afterJson
     * @return
     */
    public List<String> getPatchFileList(DirEntry beforeJson, DirEntry afterJson) {
        if (beforeJson != null) {
            List<String[]> beforeJsonStrings = jsonToList(makeFileList(beforeJson));
            List<String[]> afterJsonStrings = jsonToList(makeFileList(afterJson));
            try {
                HashMap<String, String[]> beforeHashMap = makePathHashMap(beforeJsonStrings);
                HashMap<String, String[]> afterHashMap = makePathHashMap(afterJsonStrings);

                return compareDiff(beforeHashMap, afterHashMap);

            } catch (Exception e) {
                log.error(e.getMessage());
            }
        }
        return null; // create 만들기
    }


    public List<String> compareDiff(HashMap<String, String[]> before, HashMap<String, String[]> after) {

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

    public void addDeleteList(HashMap<String, String[]> before, HashMap<String, String[]> after, List<String> diffStringList) {
        List<String> deleteList = new ArrayList<>();
        deleteList.addAll(before.keySet());
        deleteList.removeAll(after.keySet());

        for (String path : deleteList) {
            if (before.get(path)[FILE_TYPE].equals("D")) {
                before.get(path)[DIR_DIFF_TYPE] = "D";
                diffStringList.add(arrayToStringFormat(before.get(path), "D"));
            } else {
                before.get(path)[FILE_DIFF_TYPE] = "D";
                diffStringList.add(arrayToStringFormat(before.get(path), "F"));
            }
        }
    }


    public void addCreateList(HashMap<String, String[]> before, HashMap<String, String[]> after, List<String> diffStringList) {
        List<String> createList = new ArrayList<>();
        createList.addAll(after.keySet());
        createList.removeAll(before.keySet());

        for (String path : createList) {
            if (after.get(path)[FILE_TYPE].equals("D")) {
                after.get(path)[DIR_DIFF_TYPE] = "C";
                diffStringList.add(arrayToStringFormat(after.get(path), "D"));
            } else {
                after.get(path)[FILE_DIFF_TYPE] = "C";
                diffStringList.add(arrayToStringFormat(after.get(path), "F"));
            }

        }
    }

    public void addUpdateList(HashMap<String, String[]> before, HashMap<String, String[]> after, List<String> diffStringList) {
        List<String> updateList = new ArrayList<>();
        updateList.addAll(before.keySet());
        updateList.retainAll(after.keySet()); //교집합 : 빈 dir 파일이 있으면 file delete로 인해 빈 dir 파일로 create 해주어야 한다.

        for (String path : updateList) {
            if (before.get(path)[FILE_TYPE].equals("F")) {
                if (!before.get(path)[FILE_HASHING].equals(after.get(path)[FILE_HASHING])) {
                    before.get(path)[FILE_DIFF_TYPE] = "U";
                    diffStringList.add(arrayToStringFormat(before.get(path), "F"));
                }
            }
        }
    }


}
