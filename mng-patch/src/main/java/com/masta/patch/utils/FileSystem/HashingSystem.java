package com.masta.patch.utils.FileSystem;

import com.google.common.hash.HashCode;
import com.google.common.hash.Hashing;
import com.google.common.io.Files;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.File;

@Slf4j
@Service
public class HashingSystem {
    public String MD5hashing(File file){

        HashCode hc = null;
        try {
            hc = Files.asByteSource(file).hash(Hashing.md5());
        } catch (Exception e) {
            log.error(e.getMessage());
        }

        return hc.toString();

    }
}
