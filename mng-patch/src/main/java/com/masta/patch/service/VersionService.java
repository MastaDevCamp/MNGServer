package com.masta.patch.service;

import com.masta.core.response.ResponseMessage;
import com.masta.patch.dto.VersionLog;
import com.masta.patch.mapper.VersionMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class VersionService {


    private final VersionMapper versionMapper;


    public VersionService(final VersionMapper versionMapper){
        this.versionMapper = versionMapper;
    }

    public String checkRightVersion(String version) {
        String latestVersion = versionMapper.latestVersion().getVersion();
        return compareVersion(version, latestVersion);
    }

    public String getLatestVersion() {
        try {
            VersionLog versionLog = versionMapper.latestVersion();
            return versionLog.getVersion();
        } catch (Exception e) {
            log.error(e.getMessage());
            return null;
        }
    }

    public String compareVersion(String afterVersion, String beforeVersion) {
        int afterVersionInt = versionToInt(afterVersion);
        int beforeVersionInt = versionToInt(beforeVersion);
        if (afterVersionInt == -1) {   // Inappropriate format
            return ResponseMessage.NOT_VERSION_FORMAT;
        } else {
            int versionComparisionResult = afterVersionInt - beforeVersionInt;
            if (versionComparisionResult <= 0) {
                return ResponseMessage.NOT_LATEST_VERSION;
            } else {
                return ResponseMessage.SUCCESS_TO_GET_LATEST_VERSION;
            }
        }
    }

    public int versionToInt(String version) {
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
