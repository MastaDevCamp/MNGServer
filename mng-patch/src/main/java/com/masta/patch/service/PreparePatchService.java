package com.masta.patch.service;

import com.masta.core.response.ResponseMessage;
import com.masta.patch.dto.VersionLog;
import com.masta.patch.mapper.VersionMapper;
import com.masta.patch.model.DirEntry;
import com.masta.patch.utils.FileMove.LocalFileReadWrite;
import com.masta.patch.utils.JsonMaker.FullJsonMaker;
import com.masta.patch.utils.JsonMaker.PatchJsonMaker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.List;

import static com.masta.patch.utils.Compress.unzip;

@Slf4j
@Service
public class PreparePatchService {

    @Autowired
    private LocalFileReadWrite localFileReadWrite;
    @Autowired
    private PatchJsonMaker patchJsonMaker;
    @Autowired
    private FullJsonMaker fullJsonMaker;
    @Autowired
    private VersionMapper versionMapper;


    public String makePatch(MultipartFile newVersionFile, String newVersion) {
        try {
            DirEntry latestVersionFileTree = getVersionFileTree();

            DirEntry newVersionFileTree = getVersionFileTree(newVersionFile, newVersion);
            localFileReadWrite.saveJsonFile(newVersionFileTree, String.format("Full_Ver_%s.json", newVersion));
            List<String> newVersionPatchFileList = patchJsonMaker.getPatchFileList(latestVersionFileTree, newVersionFileTree);
            localFileReadWrite.saveJsonFile(newVersionPatchFileList, String.format("Patch_Ver_%s.json", newVersion));
        } catch (Exception e) {

            log.error("Fail to save json in local, " + e.getMessage());
            return ResponseMessage.FAIL_TO_SAVE_JSON_IN_LOCAL;
        }
        return ResponseMessage.SUCCESS_TO_SAVE_JSON_IN_LOCAL;
    }


    public DirEntry getVersionFileTree(MultipartFile newVersionFile, String version) {
        File newVersionZip = localFileReadWrite.saveLocal(newVersionFile);
        String unzipPath = unzip(newVersionZip);
        return fullJsonMaker.getVersionFileTree(unzipPath, version);
    }

    public DirEntry getVersionFileTree(){
        VersionLog latestVersion = versionMapper.latestVersion();
        return localFileReadWrite.getRemoteJsonToObject(latestVersion);
    }
}
