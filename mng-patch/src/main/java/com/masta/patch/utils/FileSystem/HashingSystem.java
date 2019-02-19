package com.masta.patch.utils.FileSystem;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.xml.bind.DatatypeConverter;
import java.io.File;
import java.io.RandomAccessFile;
import java.security.MessageDigest;

@Slf4j
@Component
public class HashingSystem {

    public String getMD5Hashing(File file) {
        String hashing = "";

        if (file.isFile()) {
            hashing = MD5Hashing(file);
        }
        return hashing;
    }


    public String MD5Hashing(File file) {

        String md5 = "";
        byte[] fileByte = new byte[40];

        try {
            if (file.length() > 40) {
                byte[] last = new byte[20];
                byte[] first = new byte[20];

                RandomAccessFile raf = new RandomAccessFile(file, "r");
                raf.read(first, 0, 20);
                raf.seek(file.length() - 20);
                raf.read(last, 0, 20);
                raf.close();

                // combine byte[]
                System.arraycopy(first, 0, fileByte, 0, first.length);
                System.arraycopy(last, 0, fileByte, first.length, last.length);
            } else {
                RandomAccessFile raf = new RandomAccessFile(file, "r");
                raf.read(fileByte, 0, (int) file.length());
                raf.close();
            }

            byte[] res = MessageDigest.getInstance("MD5").digest(fileByte);
            md5 = DatatypeConverter.printHexBinary(res);

        } catch (Exception e) {
            log.error(e.getMessage());
        }

        return md5;
    }

}
