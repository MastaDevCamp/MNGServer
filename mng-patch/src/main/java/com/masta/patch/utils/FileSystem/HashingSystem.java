package com.masta.patch.utils.FileSystem;

import com.google.common.hash.HashCode;
import com.google.common.hash.Hashing;
import com.google.common.io.Files;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.xml.bind.DatatypeConverter;
import java.io.File;
import java.io.RandomAccessFile;
import java.security.MessageDigest;

@Slf4j
@Component
public class HashingSystem {

    public String getMD5Hashing(File file){
        String hashing = "";

        if (file.isFile()) {
            String ext = Files.getFileExtension(file.getPath());
            if (ext.equals("txt")){
                hashing =  MD5FullHashing(file);
            }else{
                hashing = MD5PartHashing(file);
            }
        }
        return hashing;
    }


    public String MD5PartHashing(File file){

        if (file.isDirectory()) {
            return "";
        }

        String md5 = "";
        byte[] fileByte = new byte[20];

        try {
            if (file.length() > 20) {
                byte[] last = new byte[10];
                byte[] first = new byte[10];

                RandomAccessFile raf = new RandomAccessFile(file, "r");
                raf.read(first, 0, 10);
                raf.seek(file.length() - 10);
                raf.read(last, 0, 10);

                // combine byte[]
                System.arraycopy(first, 0, fileByte, 0, first.length);
                System.arraycopy(last, 0, fileByte, first.length, last.length);
            } else {
                RandomAccessFile raf = new RandomAccessFile(file, "r");
                raf.read(fileByte, 0, (int) file.length());
            }

            byte[] res = MessageDigest.getInstance("MD5").digest(fileByte);
            md5 = DatatypeConverter.printHexBinary(res);

        } catch (Exception e) {
            log.error(e.getMessage());
        }

        return md5;
    }



    public String MD5FullHashing(File file){

        if (file.isDirectory()) {
            return "";
        }

        HashCode hc = null;
        try {
            hc = Files.asByteSource(file).hash(Hashing.md5());
        } catch (Exception e) {
            log.error(e.getMessage());
        }

        return hc.toString();

    }
}
