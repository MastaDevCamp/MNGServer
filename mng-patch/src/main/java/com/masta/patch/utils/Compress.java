package com.masta.patch.utils;

import lombok.extern.slf4j.Slf4j;
import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.model.ZipParameters;
import net.lingala.zip4j.util.Zip4jConstants;
import org.apache.commons.io.FilenameUtils;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.Enumeration;
import java.util.zip.ZipEntry;

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

    public static ZipEntry getCompressedSize(String path) {
        try {
            java.util.zip.ZipFile zipFile = new java.util.zip.ZipFile(path);
            Enumeration e = zipFile.entries();
            while (e.hasMoreElements()) {
                ZipEntry entry = (ZipEntry) e.nextElement();
                if (!entry.isDirectory()) {
                    return entry;
                }
            }
            zipFile.close();
        } catch (IOException ioe) {
            System.out.println("Error opening zip file" + ioe);
        }

        return null;
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
