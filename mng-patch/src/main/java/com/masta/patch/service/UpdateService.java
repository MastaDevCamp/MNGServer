package com.masta.patch.service;

import com.masta.patch.dto.VersionLog;
import com.masta.patch.mapper.VersionMapper;
import com.masta.patch.utils.FileSystem.MergeJsonMaker;
import com.masta.patch.utils.FileSystem.model.DirEntry;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.List;

import static com.masta.patch.utils.Compress.unzip;

@Slf4j
@Service
public class UpdateService {

    private MergeJsonMaker mergeJsonMaker;
    private VersionMapper versionMapper;
    

    public UpdateService(final MergeJsonMaker mergeJsonMaker, final VersionMapper versionMapper){
        this.mergeJsonMaker = mergeJsonMaker;
        this.versionMapper = versionMapper;
    }

    public File saveLocal(MultipartFile sourceFile) {
        File file = new File(sourceFile.getName() + ".zip");
        try {
            sourceFile.transferTo(file);
            log.info("save file [" + file.getPath() + "]");
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return file;
    }

    public void updateNewVersion(String clientVersion){
        VersionLog latestVersion = versionMapper.latestVersion();

        /**
         * 해당하는 json 폴더 다운로드
         */

        List<String> mergeJsonList = mergeJsonMaker.makeMergeJson(clientVersion, latestVersion.getVersion());

        /**
         * 클라이언트에게 downLoadUrlList 넘겨주기 + json넘겨주기
         */

    }

}
