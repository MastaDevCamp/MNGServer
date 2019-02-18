package com.masta.patch.service;

import com.masta.core.response.ResponseMessage;
import com.masta.patch.dto.VersionLog;
import com.masta.patch.mapper.VersionMapper;
import com.masta.patch.utils.FileSystem.FullJsonMaker;
import com.masta.patch.utils.FileSystem.PatchJsonMaker;
import com.masta.patch.utils.FileSystem.TypeConverter;
import com.masta.patch.utils.FileSystem.model.DirEntry;
import com.masta.patch.utils.sftp.SftpServer;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.List;

import static com.masta.patch.utils.Compress.unzip;
import static com.masta.patch.utils.FileSystem.TypeConverter.JSON_EXTENTION;
import static com.masta.patch.utils.FileSystem.TypeConverter.saveJsonFile;
import static com.masta.patch.utils.VersionUtils.compareVersion;

@Slf4j
@Service
public class UploadService {

    final SftpServer sftpServer;
    final FullJsonMaker fullJsonMaker;
    final PatchJsonMaker patchJsonMaker;
    final VersionMapper versionMapper;
    final TypeConverter typeConverter;

    @Value("${local.newVersion.path}")
    private String newVersionPath;

    @Value("${local.merge.path}")
    private String mergePath;

    @Value("${local.path}")
    private String localPath;

    @Value("${sftp.root.path}")
    private String sftpRootPath;

    @Value("${local.verUpZip.path}")
    public String verUpZipPath;

    @Value("${local.zipFile.path}")
    public String zipFilePath;


    public UploadService(final SftpServer sftpServer, final FullJsonMaker fullJsonMaker,
                         final PatchJsonMaker patchJsonMaker, final VersionMapper versionMapper,
                         final TypeConverter typeConverter) {
        this.sftpServer = sftpServer;
        this.fullJsonMaker = fullJsonMaker;
        this.patchJsonMaker = patchJsonMaker;
        this.versionMapper = versionMapper;
        this.typeConverter = typeConverter;
    }

    public File saveLocal(MultipartFile sourceFile) {
        resetDir(newVersionPath);
        File file = new File(newVersionPath + sourceFile.getName() + ".zip");
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

    public String uploadNewVersion(MultipartFile sourceFile, String version) {
        resetDir(verUpZipPath);
        File localUploadFile = saveLocal(sourceFile);
        String dest = unzip(localUploadFile);

        DirEntry newFullJson = fullJsonMaker.getFileTreeList(dest, version);
        DirEntry beforeFullJson = typeConverter.getRemoteLastVersionJson(newVersionPath);


        if (beforeFullJson == null) {   // first upload version
            List<String> fileList = typeConverter.makeFileList(newFullJson);
            uploadJsonToRemote(newFullJson, null, version);
            uploadPatchFileToRemote(fileList, null);
            return ResponseMessage.UPLOAD_FIREST_VERSION;
        }

        int compareResult = compareVersion(newFullJson.getVersion(), beforeFullJson.getVersion());
        String lastestVersion = beforeFullJson.getVersion();

        switch (compareResult) {
            case 1:
                List<String> patchJson = patchJsonMaker.getPatchJson(beforeFullJson, newFullJson);
                uploadJsonToRemote(newFullJson, patchJson, version);
                uploadPatchFileToRemote(patchJson, lastestVersion);
                return ResponseMessage.SUCCESS_TO_NEW_VERSION;
            case 0:
                return ResponseMessage.ALREADY_REGISTERED_VERSION;
            case -1:
                return ResponseMessage.NOT_LAST_VERSION;
            default:
                return ResponseMessage.VERSION_ERROR;
        }


//        sftpServer.backupDir("/gameFiles/release", "/gameFiles/backupVersion");
//        sftpServer.uploadDir(new File(localPath + sourceFile.getName()), "/gameFiles/release");

    }

    public void uploadPatchFileToRemote(List<String> patchList, String lastestVersion) {

        sftpServer.init();

        if (lastestVersion != null)
            sftpServer.backupRelease("file/release", lastestVersion);

        for (String patchFile : patchList) {
            String[] fileInfo = patchFile.replace(" ", "").split("\\|");

            if ("D".equals(fileInfo[0])) {  // not dir
                continue;
            }

            if (!"D".equals(fileInfo[8])) {     // Upload only Update, create patch file
                String relativePath = fileInfo[1].replace("\\", "/").substring(0, fileInfo[1].lastIndexOf("\\"));
                sftpServer.mkdir(relativePath, "file/release");
                sftpServer.upload(new File(zipFilePath + fileInfo[1] + "." + fileInfo[2]), "file/release" + relativePath);
            }
        }

        sftpServer.disconnect();
    }

    public void uploadJsonToRemote(Object fullJson, Object patchJson, String version) {

        sftpServer.init();

        File fullJsonFile = saveJsonFile(fullJson, "Full_Ver_" + version + JSON_EXTENTION);
        File patchJsonFile = saveJsonFile(patchJson, "Patch_Ver_" + version + JSON_EXTENTION);

        try {
            sftpServer.upload(fullJsonFile, "log/full");
            sftpServer.upload(patchJsonFile, "log/patch");
        } catch (Exception e) {
            log.info("Only upload full version json");
        }

        fullJsonFile.delete();
        patchJsonFile.delete();

        String remoteFullJsonPath = sftpServer.checkFile(version, "log/full");
        String remotePatchJsonPath = sftpServer.checkFile(version, "log/patch");

        VersionLog newVersion = VersionLog.builder()
                .version(version)
                .full(remoteFullJsonPath)
                .patch(remotePatchJsonPath).build();

        versionMapper.newVersionSave(newVersion);

        sftpServer.disconnect();
    }


    public boolean checkFileExtension(String extension, MultipartFile sourceFile) {
        String sourceFileName = sourceFile.getOriginalFilename();
        return extension.equals(FilenameUtils.getExtension(sourceFileName).toLowerCase());
    }

}
