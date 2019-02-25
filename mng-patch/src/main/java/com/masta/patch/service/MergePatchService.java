package com.masta.patch.service;

import com.masta.core.response.DefaultRes;
import com.masta.core.response.ResponseMessage;
import com.masta.core.response.StatusCode;
import com.masta.patch.dto.VersionLog;
import com.masta.patch.mapper.VersionMapper;
import com.masta.patch.model.JsonType;
import com.masta.patch.utils.FileMove.NginXFileRead;
import com.masta.patch.utils.JsonMaker.MergeJsonMaker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;


@Slf4j
@Service
public class MergePatchService {

    @Value("${nginx.url}")
    private String nginXPath;

    @Value("${local.merge.path}")
    private String localMergePath;

    private MergeJsonMaker mergeJsonMaker;
    private VersionMapper versionMapper;
    private NginXFileRead nginXFileRead;
    private VersionService versionService;

    public MergePatchService(final MergeJsonMaker mergeJsonMaker, final VersionMapper versionMapper, final NginXFileRead nginXFileRead, final VersionService versionService) {
        this.mergeJsonMaker = mergeJsonMaker;
        this.versionMapper = versionMapper;
        this.nginXFileRead = nginXFileRead;
        this.versionService = versionService;
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

        // remote -> local download (using nginx)
        for (VersionLog versionLog : updateVersionList) {
            nginXFileRead.getRemoteVersionJson(versionLog, JsonType.PATCH);
        }

        List<String> updateFileList = mergeJsonMaker.makeMergeJson();
        return updateFileList;
    }

}
