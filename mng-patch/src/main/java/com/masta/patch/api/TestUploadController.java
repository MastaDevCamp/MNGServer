package com.masta.patch.api;

import com.masta.core.response.DefaultRes;
import com.masta.core.response.ResponseMessage;
import com.masta.core.response.StatusCode;
import com.masta.patch.service.UploadService;
import com.masta.patch.utils.FileSystem.FullJsonMaker;
import com.masta.patch.utils.FileSystem.MergeJsonMaker;
import com.masta.patch.utils.FileSystem.PatchJsonMaker;
import com.masta.patch.utils.FileSystem.TypeConverter;
import com.masta.patch.utils.FileSystem.model.DirEntry;
import com.masta.patch.utils.sftp.SftpServer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static com.masta.core.response.DefaultRes.FAIL_DEFAULT_RES;

@Slf4j
@RestController
@RequestMapping("testUpload")
public class TestUploadController {

    @Value("${local.newVersion.path}")
    private String newVersionPath;

    final SftpServer sftpServer;
    final UploadService uploadService;

    //mjung
    final PatchJsonMaker patchJsonMaker;
    final FullJsonMaker fullJsonMaker;
    final TypeConverter typeConverter;
    final MergeJsonMaker mergeJsonMaker;


    public TestUploadController(final SftpServer sftpServer, final UploadService uploadService,
                                final PatchJsonMaker patchJsonMaker, final FullJsonMaker fullJsonMaker,
                                final TypeConverter typeConverter, final MergeJsonMaker mergeJsonMaker) {
        this.sftpServer = sftpServer;
        this.uploadService = uploadService;
        this.patchJsonMaker = patchJsonMaker;
        this.fullJsonMaker = fullJsonMaker;
        this.typeConverter = typeConverter;
        this.mergeJsonMaker = mergeJsonMaker;
    }

    @GetMapping("test") //삭제
    public ResponseEntity upload(@RequestParam("path") final Optional<String> path, @RequestParam("dir") final String dir) {
        try {
            if (path.isPresent()) {
                log.info("create version json to POJO");
                sftpServer.init();
                //sftpServer.upload(dir, path.get());
                sftpServer.disconnect();
                return new ResponseEntity<>(HttpStatus.OK);
            }
            return new ResponseEntity(DefaultRes.res(StatusCode.OK, ResponseMessage.NOT_READ_JSON_FILE), HttpStatus.OK);

        } catch (Exception e) {
            log.error(e.getMessage());
            return new ResponseEntity<>(FAIL_DEFAULT_RES, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("testFullToPatch")
    public List<String> fullToPatch(@RequestParam("before") final String beforeVersionPath, @RequestParam("after") final String newVersionPath) {
        DirEntry beforeFullJson = typeConverter.readVersionJson(beforeVersionPath);
        DirEntry newFullJson = typeConverter.readVersionJson(newVersionPath);
        List<String> patchJson = patchJsonMaker.getPatchJson(beforeFullJson, newFullJson);
        Collections.sort(patchJson);
        return patchJson;
    }

    @GetMapping("testingFullConvert")
    public List<String> fullConvert(@RequestParam("path") final String path) {
        DirEntry jsonFile = typeConverter.readVersionJson(path);
        List<String> stringList = typeConverter.makeFileList(jsonFile);
        return stringList;
    }

    @GetMapping("testPathToMerge")
    public List<String> PatchToMerge() {
        List<String> patchJson = mergeJsonMaker.makeMergeJson();
        Collections.sort(patchJson);
        return patchJson;
    }

    @GetMapping("makeFullJson")
    public DirEntry makeFull(@RequestParam("version") final String version) {
        DirEntry newFullJson = fullJsonMaker.getFileTreeList(newVersionPath, version);
        return newFullJson;
    }


}
