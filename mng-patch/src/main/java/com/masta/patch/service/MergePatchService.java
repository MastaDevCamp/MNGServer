package com.masta.patch.service;

import com.masta.core.response.DefaultRes;
import com.masta.core.response.ResponseMessage;
import com.masta.core.response.StatusCode;
import com.masta.patch.dto.VersionLog;
import com.masta.patch.mapper.VersionMapper;
import com.masta.patch.utils.FileMove.LocalFileReadWrite;
import com.masta.patch.utils.JsonMaker.MergeJsonMaker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.List;


@Slf4j
@Service
public class MergePatchService {

    @Value("${nginx.url}")
    private String nginXPath;

    @Value("${local.path}")
    public String localPath;

    @Autowired
    private MergeJsonMaker mergeJsonMaker;
    @Autowired
    private VersionMapper versionMapper;
    @Autowired
    private VersionService versionService;
    @Autowired
    private LocalFileReadWrite localFileReadWrite;

    public DefaultRes updateNewVersion_Http(String clientVersion){
        String latestVersion = versionMapper.latestVersion().getVersion();
        String checkRightVersionResult = versionService.compareVersion(latestVersion, clientVersion);

        if (checkRightVersionResult != ResponseMessage.SUCCESS_TO_GET_LATEST_VERSION) {
            return DefaultRes.res(StatusCode.NOT_FORMAT, checkRightVersionResult);
        }

        return DefaultRes.res(StatusCode.OK, ResponseMessage.UPDATE_NEW_VERSION(clientVersion, latestVersion),getUpdateFileList_HTTP(clientVersion));

    }

    public DefaultRes updateNewVersion(String clientVersion) {

        String latestVersion = versionMapper.latestVersion().getVersion();

        String checkRightVersionResult = versionService.compareVersion(latestVersion, clientVersion);

        if (checkRightVersionResult != ResponseMessage.SUCCESS_TO_GET_LATEST_VERSION) {
            return DefaultRes.res(StatusCode.NOT_FORMAT, checkRightVersionResult);
        }

        return DefaultRes.res(StatusCode.OK, ResponseMessage.UPDATE_NEW_VERSION(clientVersion, latestVersion), getUpdateFileList(clientVersion));
    }

    public List<String> getUpdateFileList(String clientVersion) {

        int clientVersionId = versionMapper.getVersionId(clientVersion);

        List<VersionLog> updateVersionList = versionMapper.getUpdateVersionList(clientVersionId);

        localFileReadWrite.rmLocalDir(new File(localPath + "merge/"));

        for (VersionLog versionLog : updateVersionList) {
            localFileReadWrite.getRemotePatchVersionJson(versionLog);
        }

        List<String> updateFileList = mergeJsonMaker.makeMergeJson();
        return updateFileList;
    }

    public List<String> getUpdateFileList_HTTP(String clientVersion){
        int clientVersionId = versionMapper.getVersionId(clientVersion);

        List<VersionLog> updateVersionList = versionMapper.getUpdateVersionList(clientVersionId);
        List<String> updateFileList = null;
        if(updateVersionList != null){
             updateFileList = mergeJsonMaker.makeMergeJson_HTTP(updateVersionList);
        }
        return updateFileList;
    }



}
