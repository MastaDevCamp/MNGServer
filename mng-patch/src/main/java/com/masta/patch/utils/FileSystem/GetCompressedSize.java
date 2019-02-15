package com.masta.patch.utils.FileSystem;

/*
	Get Compressed Size of Zip Entry Example
	This Java example shows how to get compressed size of particular entry
	(i.e. file or directory) using getCompressedSize method of
	Java ZipEntry class.
*/

import java.io.File;
import java.io.IOException;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;


public class GetCompressedSize {

    public static void main(String args[])
    {
        try {
            java.util.zip.ZipFile zipFile = new java.util.zip.ZipFile("C:\\WorkSpace\\verUpZip\\sourceFile_dir4_dir5_file2.txt.zip");
            Enumeration e = zipFile.entries();
            while (e.hasMoreElements()) {
                ZipEntry entry = (ZipEntry) e.nextElement();
                if(!entry.isDirectory()){
                    String entryName = entry.getName();
                    long compressedSize = entry.getCompressedSize();
                    long originalSize = entry.getSize();
                    System.out.println(entry.getName());
                    System.out.println("FileName : " + entryName);
                    System.out.println("compressedSize : " + compressedSize);
                    System.out.println("originalSize : " + originalSize);
                    System.out.println(entry.getCrc());
                }

            }

            zipFile.close();

        } catch (IOException ioe) {
            System.out.println("Error opening zip file" + ioe);
        }
    }

}
