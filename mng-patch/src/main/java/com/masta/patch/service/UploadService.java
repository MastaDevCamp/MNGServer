package com.masta.patch.service;

import com.masta.patch.utils.sftp.SftpServer;
import lombok.extern.slf4j.Slf4j;
import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;

@Slf4j
@Service
public class UploadService {

    final SftpServer sftpServer;

    @Value("${local.path}")
    private String localPath;

    public UploadService(final SftpServer sftpServer) {
        this.sftpServer = sftpServer;
    }

    public void uploadNewVersion(MultipartFile sourceFile) {
        File file = new File(localPath + sourceFile.getName() + ".zip");
        try {
            sourceFile.transferTo(file);
            log.info("save file [" + file.getPath() + "]");
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        unzip(file);
        sftpServer.init();
        sftpServer.backupDir("/gameFiles/release", "/gameFiles/backupVersion");
        sftpServer.uploadDir(new File(localPath + sourceFile.getName()), "/gameFiles/release");
    }

    public static void unzip(File file) {
        String source = file.getPath();
        String destination = FilenameUtils.removeExtension(file.getPath());
        try {
            ZipFile zipFile = new ZipFile(source);
            zipFile.extractAll(destination);
        } catch (ZipException e) {
            e.printStackTrace();
        }
    }


    public boolean checkFileExtension(String extension, MultipartFile sourceFile) {
        String sourceFileName = sourceFile.getOriginalFilename();
        return extension.equals(FilenameUtils.getExtension(sourceFileName).toLowerCase());
    }


}
