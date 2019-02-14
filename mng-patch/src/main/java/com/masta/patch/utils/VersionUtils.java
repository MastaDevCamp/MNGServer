package com.masta.patch.utils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class VersionUtils {

    public static void main(String[] args) {
        System.out.println(VersionUtils.versionToInt("0.0.1"));
        System.out.println(VersionUtils.versionToInt("1.0.12"));
    }

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
            res *= 1000;
            res = res + Integer.parseInt(ver);
        }
        return res;
    }
}
