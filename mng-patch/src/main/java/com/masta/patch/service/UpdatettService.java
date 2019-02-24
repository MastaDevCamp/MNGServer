package com.masta.patch.service;

import com.masta.core.response.DefaultRes;
import com.masta.core.response.ResponseMessage;
import com.masta.core.response.StatusCode;
import com.masta.patch.dto.VersionLog;
import com.masta.patch.mapper.VersionMapper;
import com.masta.patch.utils.JsonMaker.MergeJsonMaker;
import com.masta.patch.utils.TypeConverter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;

import static com.masta.patch.model.VersionCheckResultType.checkRightVersion;

@Slf4j
@Service
public class UpdatettService {

    @Value("${nginx.url}")
    private String nginXPath;

    @Value("${local.merge.path}")
    private String localMergePath;

    final static String PATCH_NAME = "Patch_Ver_";

    private MergeJsonMaker mergeJsonMaker;
    private VersionMapper versionMapper;


    public UpdatettService(final MergeJsonMaker mergeJsonMaker, final VersionMapper versionMapper) {
        this.mergeJsonMaker = mergeJsonMaker;
        this.versionMapper = versionMapper;
    }

    public DefaultRes updateNewVersion(String clientVersion) {

        String latestVersion = versionMapper.latestVersion().getVersion();
        switch (checkRightVersion(latestVersion, clientVersion)) {
            case NOT_LATEST_VERSION:
                return DefaultRes.res(StatusCode.NOT_FORMAT, ResponseMessage.NOT_ZIP_FILE);
            case NOT_VERSION_FORMAT:
                return DefaultRes.res(StatusCode.NOT_FORMAT, ResponseMessage.NOT_VERSION_FORMAT);
            case LATEST_VERSION:
                return DefaultRes.res(StatusCode.OK, ResponseMessage.UPDATE_NEW_VERSION(clientVersion, latestVersion), getUpdateFileList(clientVersion));
        }
        return DefaultRes.FAIL_DEFAULT_RES;
    }

    public List<String> getUpdateFileList(String clientVersion) {

        int clientVersionId = versionMapper.getVersionId(clientVersion);

        List<VersionLog> updateVersionList = versionMapper.getUpdateVersionList(clientVersionId);

        // remote -> local download (using nginx)
        for (VersionLog versionLog : updateVersionList) {
            downLoadVersionJson(versionLog.getVersion(), versionLog.getPatch());
        }

        List<String> updateFileList = mergeJsonMaker.makeMergeJson();

        return updateFileList;
    }


    public void downLoadVersionJson(String versionName, String versionPath) {
        try {
            File file = new File(localMergePath + PATCH_NAME + versionName + TypeConverter.JSON_EXTENTION);
            log.info(file.toString());
            URL url = new URL(nginXPath + versionPath);
            log.info(url.toString());
            URLConnection connection = url.openConnection();
            InputStream is = connection.getInputStream();
            FileUtils.copyInputStreamToFile(is, file);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }


}
