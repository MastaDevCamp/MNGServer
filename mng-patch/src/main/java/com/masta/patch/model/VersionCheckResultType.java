package com.masta.patch.model;

public enum VersionCheckResultType {
    NOT_LATEST_VERSION,
    NOT_VERSION_FORMAT,
    LATEST_VERSION;

    public static VersionCheckResultType checkRightVersion(String afterVersion, String beforeVersion){
        int afterVersionInt = versionToInt(afterVersion);
        int beforeVersionInt = versionToInt(beforeVersion);
        if (afterVersionInt == -1) {   // Inappropriate format
            return VersionCheckResultType.NOT_VERSION_FORMAT;
        } else {
            int versionComparisionResult = afterVersionInt - beforeVersionInt;
            if (versionComparisionResult <= 0) {
                return VersionCheckResultType.NOT_LATEST_VERSION;
            } else {
                return VersionCheckResultType.LATEST_VERSION;
            }
        }
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
