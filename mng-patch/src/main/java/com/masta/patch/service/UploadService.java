package com.masta.patch.service;

import com.masta.patch.dto.VersionLog;
import com.masta.patch.mapper.VersionMapper;
import com.masta.patch.utils.FileSystem.FullJsonMaker;
import com.masta.patch.utils.FileSystem.PatchJsonMaker;
import com.masta.patch.utils.FileSystem.TypeConverter;
import com.masta.patch.utils.FileSystem.model.DirEntry;
import com.masta.patch.utils.sftp.SftpServer;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.List;

import static com.masta.patch.utils.Compress.unzip;

@Slf4j
@Service
public class UploadService {

    final static String PATCH_NAME = "patch_ver";
    final static String FULL_NAME = "full_ver";

    final SftpServer sftpServer;
    final FullJsonMaker fullJsonMaker;
    final PatchJsonMaker patchJsonMaker;
    final VersionMapper versionMapper;
    final TypeConverter typeConverter;

    @Value("${PMS.url}")
    private String pmsPath;

    @Value("${local.path}")
    private String localPath;

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
        File file = new File(localPath + sourceFile.getName() + ".zip");
        try {
            sourceFile.transferTo(file);
            log.info("save file [" + file.getPath() + "]");
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return file;
    }

    public void uploadNewVersion(MultipartFile sourceFile, String version) {
        File localUploadFile = saveLocal(sourceFile);
        String dest = unzip(localUploadFile);

        DirEntry beforeFullJson = typeConverter.getRemoteLastVersionJson();
        DirEntry newFullJson = fullJsonMaker.getFileTreeList(dest, version);

        List<String> newPatchJson = patchJsonMaker.getPatchJson(beforeFullJson, newFullJson);

        typeConverter.saveJsonFile(newFullJson, PATCH_NAME + version + typeConverter.JSON_EXTENTION);

        sftpServer.init();
        sftpServer.backupDir("/gameFiles/release", "/gameFiles/backupVersion");
        sftpServer.uploadDir(new File(localPath + sourceFile.getName()), "/gameFiles/release");

        VersionLog versionLog = VersionLog.builder()
                .version(version)
                .full(newFullJson.getPath())
                .patch(pmsPath + "patch" + version + typeConverter.JSON_EXTENTION).build();

        versionMapper.newVersionSave(versionLog);
    }


    public boolean checkFileExtension(String extension, MultipartFile sourceFile) {
        String sourceFileName = sourceFile.getOriginalFilename();
        return extension.equals(FilenameUtils.getExtension(sourceFileName).toLowerCase());
    }

}
