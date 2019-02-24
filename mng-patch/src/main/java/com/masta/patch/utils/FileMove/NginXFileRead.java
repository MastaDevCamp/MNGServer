package com.masta.patch.utils.FileMove;

import com.masta.patch.dto.VersionLog;
import com.masta.patch.model.JsonType;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

@Component
@Slf4j
public class NginXFileRead {

    @Value("${nginx.url}")
    private String nginXPath;

    @Value("${local.merge.path}")
    private String localMergePath;

    @Value("${local.path}")
    public String localPath;

    public File getRemoteLatestVersionJson(VersionLog version, JsonType jsonType) {
        File file = null;
        if (version != null) {
            try {
                if(jsonType == JsonType.FULL){
                    file = new File(localPath + String.format("Full_Ver_%s.json", version.getVersion()));
                }else if(jsonType == JsonType.PATCH){
                    file = new File(localMergePath + String.format("Patch_Ver_%s.json", version.getVersion()));
                }else{
                    return null;  // throw error
                }
                URL url = new URL(nginXPath + version.getFull());
                URLConnection connection = url.openConnection();
                InputStream is = connection.getInputStream();
                FileUtils.copyInputStreamToFile(is, file);
            } catch (Exception e) {
                log.error(e.getMessage());
            }
        }
        return file;
    }

}