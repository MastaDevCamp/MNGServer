package com.masta.patch.service;

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

import static com.masta.patch.utils.Compress.unzip;

@Slf4j
@Service
public class UploadService {


    final static String JSON_EXTENTION = ".json";

    final SftpServer sftpServer;
    final FullJsonMaker fullJsonMaker;
    final PatchJsonMaker patchJsonMaker;
    final VersionMapper versionMapper;
    final TypeConverter typeConverter;

    @Value("${PMS.url}")
    private String pmsPath;

    @Value("${local.newVersion.path}")
    private String newVersionPath;

    @Value("${local.merge.path}")
    private String mergePath;

    @Value("${local.path}")
    private String localPath;


    @Value("${sftp.root.path}")
    private String sftpRootPath;


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


    public void resetDir(String path) {
        try {
            FileUtils.deleteDirectory(new File(path));
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        new File(path).mkdirs();
    }

    public void uploadNewVersion(MultipartFile sourceFile, String version) {
        File localUploadFile = saveLocal(sourceFile);
        String dest = unzip(localUploadFile);

        DirEntry newFullJson = fullJsonMaker.getFileTreeList(dest, version);
        DirEntry beforeFullJson = typeConverter.getRemoteLastVersionJson(newVersionPath);

        File patchJson = patchJsonMaker.getPatchJson(beforeFullJson, newFullJson);

        sftpServer.init();
        sftpServer.uploadDir(patchJson, sftpRootPath + "log/patch");

//
//        sftpServer.backupDir("/gameFiles/release", "/gameFiles/backupVersion");
//        sftpServer.uploadDir(new File(localPath + sourceFile.getName()), "/gameFiles/release");

        VersionLog versionLog = VersionLog.builder()
                .version(version)
                .full(newFullJson.getPath())
                .patch(pmsPath + "patch" + version + JSON_EXTENTION).build();

        versionMapper.newVersionSave(versionLog);
    }


    public boolean checkFileExtension(String extension, MultipartFile sourceFile) {
        String sourceFileName = sourceFile.getOriginalFilename();
        return extension.equals(FilenameUtils.getExtension(sourceFileName).toLowerCase());
    }


}
