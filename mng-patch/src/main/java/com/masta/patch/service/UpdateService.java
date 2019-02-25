package com.masta.patch.service;

import com.masta.core.response.DefaultRes;
import com.masta.core.response.ResponseMessage;
import com.masta.core.response.StatusCode;
import com.masta.patch.dto.VersionLog;
import com.masta.patch.mapper.VersionMapper;
import com.masta.patch.model.DirEntry;
import com.masta.patch.utils.FileSystem.FullJsonMaker;
import com.masta.patch.utils.FileSystem.PatchJsonMaker;
import com.masta.patch.utils.FileSystem.TypeConverter;
import com.masta.patch.utils.SftpServer;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.io.File;
import java.util.List;

import static com.masta.patch.utils.Compress.unzip;

@Slf4j
@Service
public class UpdateService {

    final SftpServer sftpServer;
    final FullJsonMaker fullJsonMaker;
    final PatchJsonMaker patchJsonMaker;
    final VersionMapper versionMapper;
    final TypeConverter typeConverter;

    @Value("${local.path}")
    private String localPath;


    public UpdateService(final SftpServer sftpServer, final FullJsonMaker fullJsonMaker,
                         final PatchJsonMaker patchJsonMaker, final VersionMapper versionMapper,
                         final TypeConverter typeConverter) {
        this.sftpServer = sftpServer;
        this.fullJsonMaker = fullJsonMaker;
        this.patchJsonMaker = patchJsonMaker;
        this.versionMapper = versionMapper;
        this.typeConverter = typeConverter;
    }

    public File saveLocal(MultipartFile sourceFile) {
        resetDir(localPath);
        File file = new File(localPath + sourceFile.getName() + ".zip");
        try {
            sourceFile.transferTo(file);
            log.info("save file [" + file.getPath() + "]");
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return file;
    }

    public static void resetDir(String path) {
        try {
            FileUtils.deleteDirectory(new File(path));
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        new File(path).mkdirs();
    }
    ////////////////////////////////////////////////////////////////////////////////////////////

    @Transactional
    public DefaultRes updateVersion(MultipartFile newVersionFile, String newVersion) {
        try {
            // 1. 업로드 파일 zip인지 확인
            if (!checkFileType(newVersionFile, "zip")) {
                return DefaultRes.res(StatusCode.NOT_FORMAT, ResponseMessage.NOT_ZIP_FILE);
            }

            // 2. 입력 받은 버전이 포멧하고 최신인지 확인
            String checkRightVersionResult = checkRightVersion(newVersion);
            if (checkRightVersionResult != ResponseMessage.SUCCESS_TO_GET_LATEST_VERSION) {
                return DefaultRes.res(StatusCode.NOT_FORMAT, checkRightVersionResult);
            }

            // 3. 패치 만들기(FULL JSON, PATCH JSON, PATCH FILES)
            String makePatchResult = makePatch(newVersionFile, newVersion);
            if (makePatchResult != ResponseMessage.SUCCESS_TO_SAVE_JSON_IN_LOCAL) {
                return DefaultRes.res(StatusCode.INTERNAL_SERVER_ERROR, checkRightVersionResult);
            }

            // 4. 업로드 하기(Patch_Ver_[newVersion].json, Full_Ver_[newVersion].json, Patch Files to file/history/[newVersion], Full Files to file/release)
            String uploadPatchResult = uploadPatch(newVersion);
            if (uploadPatchResult != ResponseMessage.SUCCESS_TO_UPLOAD_PATCH) {
                return DefaultRes.res(StatusCode.INTERNAL_SERVER_ERROR, uploadPatchResult);
            }

            return DefaultRes.res(StatusCode.OK, ResponseMessage.SUCCESS_TO_NEW_VERSION);
        } catch (Exception e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            log.error(e.getMessage());
            initSftpServer(newVersion);
            return DefaultRes.res(StatusCode.INTERNAL_SERVER_ERROR, ResponseMessage.DB_ERROR);
        }
    }

    public void initSftpServer(String version) {
        sftpServer.remove(String.format("log/full/Full_Ver_%s.json", version));
        sftpServer.remove(String.format("log/patch/Patch_Ver_%s.json", version));
        sftpServer.rmDir("file/history/" + version);
        sftpServer.rmDir("file/release");
        sftpServer.backupDir("file/backup", "file/release");
    }

    ///////////////////////////////////            // 4. 업로드 하기(Patch_Ver_[newVersion].json, Full_Ver_[newVersion].json, Patch Files to file/history/[newVersion], Full Files to file/release)
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

            List<String> fullFileList = typeConverter.fullJsonToFileList(fullJsonPath);
            uploadToRemote(fullFileList, "file/release");

            List<String> patchFileList = typeConverter.patchJsonToFileList(pathJsonPath);
            uploadToRemote(patchFileList, "file/history/" + newVersion);
        } catch (Exception e) {
            log.error("Fail to upload files to SFTP, " + e.getMessage());
            return ResponseMessage.FAIL_TO_UPLOAD_FILES_TO_SFTP;
        }
        return ResponseMessage.SUCCESS_TO_UPLOAD_PATCH;
    }

    public void uploadToRemote(List<String> uploadFileList, String remotePath) {
        sftpServer.init();
        sftpServer.backupDir("file/release", "file/backup");
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


    ////////////////////////////// // 3. 패치 만들기(FULL JSON, PATCH JSON, PATCH FILES)
    public String makePatch(MultipartFile newVersionFile, String newVersion) {
        try {
            DirEntry latestVersionFileTree = getVersionFileTree();
            DirEntry newVersionFileTree = getVersionFileTree(newVersionFile, newVersion);
            typeConverter.saveJsonFile(newVersionFileTree, String.format("Full_Ver_%s.json", newVersion));
            List<String> newVersionPatchFileList = patchJsonMaker.getPatchFileList(latestVersionFileTree, newVersionFileTree);
            typeConverter.saveJsonFile(newVersionPatchFileList, String.format("Patch_Ver_%s.json", newVersion));
        } catch (Exception e) {

            log.error("Fail to save json in local, " + e.getMessage());
            return ResponseMessage.FAIL_TO_SAVE_JSON_IN_LOCAL;
        }
        return ResponseMessage.SUCCESS_TO_SAVE_JSON_IN_LOCAL;
    }


    public DirEntry getVersionFileTree(MultipartFile newVersionFile, String version) {
        File newVersionZip = saveLocal(newVersionFile);
        String unzipPath = unzip(newVersionZip);
        return fullJsonMaker.getVersionFileTree(unzipPath, version);
    }

    public DirEntry getVersionFileTree() {
        return typeConverter.getRemoteLatestVersionJson();
    }


    ////////////////////////////// 2. 버전이 맞는지 확인(포멧하고 최신 버전인지)
    public String checkRightVersion(String version) {
        String latestVersion = getLatestVersion();
        return compareVersion(version, latestVersion);
    }

    public String getLatestVersion() {
        try {
            VersionLog versionLog = versionMapper.latestVersion();
            return versionLog.getVersion();
        } catch (Exception e) {
            log.error(e.getMessage());
            return null;
        }
    }

    public String compareVersion(String newVersion, String latestVersion) {
        int newVersionInt = versionToInt(newVersion);
        int latestVersionInt = versionToInt(latestVersion);
        if (newVersionInt == -1) {   // Inappropriate format
            return ResponseMessage.NOT_VERSION_FORMAT;
        } else {
            int versionComparisionResult = newVersionInt - latestVersionInt;
            if (versionComparisionResult <= 0) {
                return ResponseMessage.NOT_LATEST_VERSION;
            } else {
                return ResponseMessage.SUCCESS_TO_GET_LATEST_VERSION;
            }
        }
    }

    public int versionToInt(String version) {
        if (version == null) {
            return 0;
        }
        String[] versions = version.split("\\.");
        int res = 0;
        for (String ver : versions) {
            if (Integer.parseInt(ver) > 10) {
                return -1;
            }
            res *= 100;
            res = res + Integer.parseInt(ver);
        }
        return res;
    }

    ////////////////////////////// 1. 업로드 받은 파일이 zip파일인지 확인
    public boolean checkFileType(MultipartFile sourceFile, String fileType) {
        String sourceFileName = sourceFile.getOriginalFilename();
        return fileType.equals(FilenameUtils.getExtension(sourceFileName).toLowerCase());
    }


    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////


}