package com.masta.patch.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class VersionUtils {


    public static int compareVersion(String version1, String version2) {
        int verInt1 = versionToInt(version1);
        int verInt2 = versionToInt(version2);

        return verInt1 > verInt2 ? 1 : (verInt1 < verInt2 ? -1 : 0);
    }

    public static int versionToInt(String version) {
        if (version == null) {
            return 0;
        }
        String[] versions = version.split("\\.");
        int res = 0;
        for (String ver : versions) {
            if (Integer.parseInt(ver) > 10) {
                return -1;
            }
            res *= 100;
            res = res + Integer.parseInt(ver);
        }
        return res;
    }
}
