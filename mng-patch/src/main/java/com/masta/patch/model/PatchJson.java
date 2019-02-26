package com.masta.patch.model;

import com.masta.patch.dto.VersionLog;
import com.masta.patch.mapper.VersionMapper;
import com.masta.patch.utils.FileMove.LocalFileReadWrite;
import com.masta.patch.utils.JsonMaker.MergeJsonMaker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.List;

@Slf4j
@Service
public class PatchJson {

    @Autowired
    private VersionMapper versionMapper;
    @Autowired
    private LocalFileReadWrite localFileReadWrite;
    @Autowired
    private MergeJsonMaker mergeJsonMaker;

    public static HashMap<String, List<String>> patchJson;

    @PostConstruct
    public void init() {
        patchJson = new HashMap<>();
    }

    public String getPatchJson() {
        try {

            int clientVersionId = versionMapper.getVersionId("0.0.1");
            List<VersionLog> updateVersionList = versionMapper.getUpdateVersionList(clientVersionId);

            for (VersionLog versionLog : updateVersionList) {
                localFileReadWrite.getRemotePatchVersionJson(versionLog);
                List<String> fileList = mergeJsonMaker.getPatchJsonContent(versionLog.getPatch());

                patchJson.put(versionLog.getVersion(), fileList);
            }
            return patchJson.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "";
    }
}
