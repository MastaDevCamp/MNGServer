package com.masta.patch.utils;

import lombok.extern.slf4j.Slf4j;
import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.model.ZipParameters;
import net.lingala.zip4j.util.Zip4jConstants;
import org.apache.commons.io.FilenameUtils;
import org.springframework.stereotype.Component;

import java.io.File;

@Slf4j
@Component
public class Compress {

    private static String originalFilePath = "C:\\WorkSpace\\newVersion\\";

    private static String zipFilePath = "C:\\WorkSpace\\ZipDir\\";

    public static String zip(File file) {

        String relPath = file.getParent().replace(originalFilePath, "") + "/";
        String destination = file.getPath();
        new File(zipFilePath + relPath).mkdirs();
        String source = zipFilePath + relPath + file.getName() + ".zip";

        File destFile = new File(destination);

        try {
            ZipFile zipFile = new ZipFile(source);
            ZipParameters parameters = new ZipParameters();
            parameters.setCompressionMethod(Zip4jConstants.COMP_DEFLATE);
            parameters.setCompressionLevel(Zip4jConstants.DEFLATE_LEVEL_NORMAL);
            zipFile.addFile(destFile, parameters);
        } catch (ZipException e) {
            e.printStackTrace();
        }

        return source;
    }

    public static String unzip(File file) {
        String source = file.getPath();
        String destination = FilenameUtils.removeExtension(file.getPath());
        try {
            ZipFile zipFile = new ZipFile(source);
            zipFile.extractAll(destination);
        } catch (ZipException e) {
            e.printStackTrace();
        }

        return destination;
    }
}
