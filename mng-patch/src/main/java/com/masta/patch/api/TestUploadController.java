package com.masta.patch.api;

import com.masta.core.response.DefaultRes;
import com.masta.core.response.ResponseMessage;
import com.masta.core.response.StatusCode;
import com.masta.patch.service.UploadService;
import com.masta.patch.utils.sftp.SftpServer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

import static com.masta.core.response.DefaultRes.FAIL_DEFAULT_RES;

@Slf4j
@RestController
@RequestMapping("testUpload")
public class TestUploadController {

    final SftpServer sftpServer;
    final UploadService uploadService;

    public TestUploadController(final SftpServer sftpServer, final UploadService uploadService) {
        this.sftpServer = sftpServer;
        this.uploadService = uploadService;
    }

    @GetMapping("test")
    public ResponseEntity upload(@RequestParam("path") final Optional<String> path, @RequestParam("dir") final String dir) {
        try {
            if (path.isPresent()) {
                log.info("create version json to POJO");
                sftpServer.init();
                sftpServer.upload(dir, path.get());
                sftpServer.disconnect();
                return new ResponseEntity<>(HttpStatus.OK);
            }
            return new ResponseEntity(DefaultRes.res(StatusCode.OK, ResponseMessage.NOT_READ_JSON_FILE), HttpStatus.OK);

        } catch (Exception e) {
            log.error(e.getMessage());
            return new ResponseEntity<>(FAIL_DEFAULT_RES, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("newVersion")
    public ResponseEntity newVersion(@RequestPart MultipartFile sourceFile) {
        try {
            if (uploadService.checkFileExtension("zip", sourceFile)) {
                uploadService.uploadNewVersion(sourceFile);
            } else {
                log.info("File is not .zip file.");
                return new ResponseEntity(DefaultRes.res(StatusCode.OK, ResponseMessage.NOT_ZIP_FILE), HttpStatus.OK);
            }
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            log.error(e.getMessage());
            return new ResponseEntity<>(FAIL_DEFAULT_RES, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
