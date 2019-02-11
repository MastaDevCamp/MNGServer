package com.masta.patch.utils;

import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.model.ZipParameters;
import net.lingala.zip4j.util.Zip4jConstants;
import org.apache.commons.io.FilenameUtils;

import java.io.File;

public class Compress {

    public static String zip(File file) {
        String source = file.getPath();
        String destination = file.getPath() + ".zip";

        try {
            ZipFile zipFile = new ZipFile(source);
            ZipParameters parameters = new ZipParameters();
            parameters.setCompressionMethod(Zip4jConstants.COMP_DEFLATE);
            parameters.setCompressionLevel(Zip4jConstants.DEFLATE_LEVEL_NORMAL);
            zipFile.addFolder(destination, parameters);
        } catch (ZipException e) {
            e.printStackTrace();
        }

        return destination;

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
