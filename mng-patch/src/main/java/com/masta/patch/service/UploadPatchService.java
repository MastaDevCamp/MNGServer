package com.masta.patch.service;

import com.masta.core.response.ResponseMessage;
import com.masta.patch.dto.VersionLog;
import com.masta.patch.mapper.VersionMapper;
import com.masta.patch.utils.FileMove.LocalFileReadWrite;
import com.masta.patch.utils.FileMove.SftpServer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.List;

@Slf4j
@Service
public class UploadPatchService {

    @Value("${local.path}")
    private String localPath;

    @Autowired
    private LocalFileReadWrite localFileReadWrite;
    @Autowired
    private SftpServer sftpServer;
    @Autowired
    private VersionMapper versionMapper;

    public String uploadPatch(String newVersion) {

        try {
            UploadJson(newVersion);
        } catch (Exception e) {
            log.error("Fail to Upload jsons to SFTP, " + e.getMessage());
            return ResponseMessage.FAIL_TO_UPLOAD_JSON_TO_SFTP;
        }

        try {
            String fullJsonPath = localPath + String.format("Full_Ver_%s.json", newVersion);
            String pathJsonPath = localPath + String.format("Patch_Ver_%s.json", newVersion);

            List<String> fullFileList = localFileReadWrite.fullJsonToFileList(fullJsonPath);
            uploadToRemote(fullFileList, "file/release", true);

            List<String> patchFileList = localFileReadWrite.patchJsonToFileList(pathJsonPath);
            uploadToRemote(patchFileList, "file/history/" + newVersion, false);
        } catch (Exception e) {
            log.error("Fail to upload files to SFTP, " + e.getMessage());
            return ResponseMessage.FAIL_TO_UPLOAD_FILES_TO_SFTP;
        }
        return ResponseMessage.SUCCESS_TO_UPLOAD_PATCH;
    }

    public void uploadToRemote(List<String> uploadFileList, String remotePath, boolean isBackup) {
        sftpServer.init();
        if (isBackup)
            sftpServer.backupDir(remotePath, "file/backup");
        sftpServer.rmDir(remotePath);
        sftpServer.mkdir(remotePath);

        for (String patchFile : uploadFileList) {
            String[] fileInfo = patchFile.replace(" ", "").split("\\|");

            if ("D".equals(fileInfo[0])) {  // not dir
                continue;
            }

            if (!"D".equals(fileInfo[8])) {     // Upload only Update, create patch file
                String relativePath = fileInfo[1].replace("\\", "/").substring(0, fileInfo[1].lastIndexOf("\\"));
                sftpServer.mkdir(relativePath, remotePath);
                sftpServer.upload(new File(localPath + "PatchZip/" + fileInfo[1] + "." + fileInfo[2]), remotePath + relativePath);
            }

        }

        sftpServer.disconnect();
    }


    public String UploadJson(String newVersion) {
        String remoteFullJsonPath;
        String remotePatchJsonPath;

        try {
            sftpServer.init();

            File fullJsonFile = new File(localPath + String.format("Full_Ver_%s.json", newVersion));
            File patchJsonFile = new File(localPath + String.format("Patch_Ver_%s.json", newVersion));

            sftpServer.upload(fullJsonFile, "log/full");
            sftpServer.upload(patchJsonFile, "log/patch");

            remoteFullJsonPath = sftpServer.checkFile(newVersion, "log/full");
            remotePatchJsonPath = sftpServer.checkFile(newVersion, "log/patch");

            sftpServer.disconnect();
        } catch (Exception e) {
            log.error("Fail to uplad Json, " + e.getMessage());
            return ResponseMessage.FAIL_TO_UPLOAD_JSON_TO_SFTP;
        }

        try {
            VersionLog newVersionLog = VersionLog.builder()
                    .version(newVersion)
                    .full(remoteFullJsonPath)
                    .patch(remotePatchJsonPath).build();

            versionMapper.newVersionSave(newVersionLog);
        } catch (Exception e) {
            log.error("Fail to input DB, " + e.getMessage());
            return ResponseMessage.FAIL_TO_INSERT_JSON_DB;
        }

        return ResponseMessage.SUCCESS_TO_INSERT_JSON_DB;
    }

    public void initSftpServer(String version) {
        sftpServer.remove(String.format("log/full/Full_Ver_%s.json", version));
        sftpServer.remove(String.format("log/patch/Patch_Ver_%s.json", version));
        sftpServer.rmDir("file/history/" + version);
        sftpServer.rmDir("file/release");
        sftpServer.backupDir("file/backup", "file/release");
    }
}
