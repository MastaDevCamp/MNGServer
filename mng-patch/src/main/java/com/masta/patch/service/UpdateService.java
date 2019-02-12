package com.masta.patch.service;

import com.masta.patch.dto.VersionLog;
import com.masta.patch.mapper.VersionMapper;
import com.masta.patch.utils.FileSystem.MergeJsonMaker;
import com.masta.patch.utils.FileSystem.TypeConverter;
import com.masta.patch.utils.FileSystem.model.DirEntry;
import com.masta.patch.utils.sftp.SftpServer;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;

@Slf4j
@Service
public class UpdateService {

    @Value("${nginx.url}")
    private String nginXPath;

    @Value("${local.merge.path}")
    private String localMergePath;

    final static String PATCH_NAME = "patch_ver_";

    private MergeJsonMaker mergeJsonMaker;
    private VersionMapper versionMapper;
    private SftpServer sftpServer;

    public UpdateService(final MergeJsonMaker mergeJsonMaker, final VersionMapper versionMapper,
                         final SftpServer sftpServer){
        this.mergeJsonMaker = mergeJsonMaker;
        this.versionMapper = versionMapper;
        this.sftpServer = sftpServer;
    }

    public List<String> updateNewVersion(String clientVersion){

        String latestVersion = null;

        int clientVersionId = versionMapper.getVersionId(clientVersion);

        List<VersionLog> updateVersionList = versionMapper.getUpdateVersionList(clientVersionId);

        /**
         * 해당하는 json 폴더 다운로드
         */
        for(VersionLog versionLog : updateVersionList){
            downLoadVersionJson(versionLog.getVersion(), versionLog.getPatch());
            latestVersion = versionLog.getVersion();
        }

        List<String> updateFileList =  mergeJsonMaker.makeMergeJson();

        log.info(clientVersion+ " to " +latestVersion + " update File List ");

        return updateFileList;


    }


    public void downLoadVersionJson(String versionName, String versionPath) {

        try{
            File file = new File(localMergePath + PATCH_NAME +versionName + TypeConverter.JSON_EXTENTION);
            log.info(file.toString());
            URL url = new URL(nginXPath + versionPath);
            log.info(url.toString());
            URLConnection connection = url.openConnection();
            InputStream is = connection.getInputStream();
            FileUtils.copyInputStreamToFile(is, file);
        }catch (Exception e){
            log.error(e.getMessage());
        }


    }


}
