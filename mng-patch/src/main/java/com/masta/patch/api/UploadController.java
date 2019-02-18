package com.masta.patch.api;

import com.masta.core.response.DefaultRes;
import com.masta.core.response.ResponseMessage;
import com.masta.core.response.StatusCode;
import com.masta.patch.service.UploadService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import static com.masta.core.response.DefaultRes.FAIL_DEFAULT_RES;

@CrossOrigin
@Slf4j
@RestController
@RequestMapping("upload")
public class UploadController {

    final UploadService uploadService;

    public UploadController(final UploadService uploadService) {
        this.uploadService = uploadService;
    }


    @Transactional
    @PostMapping("newVersion")
    public ResponseEntity uploadNewVersion(@RequestPart final MultipartFile sourceFile, @RequestParam("version") final String version) {
        try {
            log.info(version);
            if (!version.isEmpty() && uploadService.checkFileExtension("zip", sourceFile)) {
                return new ResponseEntity(uploadService.uploadNewVersion(sourceFile, version), HttpStatus.OK);
            } else {
                log.info("File is not .zip file.");
                return new ResponseEntity(DefaultRes.res(StatusCode.OK, ResponseMessage.NOT_ZIP_FILE), HttpStatus.OK);
            }
        } catch (Exception e) {
            log.error(e.getMessage());
            return new ResponseEntity<>(FAIL_DEFAULT_RES, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
