package com.masta.patch.controller;

import com.masta.core.response.DefaultRes;
import com.masta.core.response.ResponseMessage;
import com.masta.core.response.StatusCode;
import com.masta.patch.model.JsonType;
import com.masta.patch.service.AdminClientService;
import com.masta.patch.service.UploadService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import static com.masta.core.response.DefaultRes.FAIL_DEFAULT_RES;

@AllArgsConstructor
@CrossOrigin
@RequestMapping("admin")
@RestController
@Slf4j
public class AdminController {

    private final AdminClientService adminClientService;
    private final UploadService uploadService;

    @GetMapping("all")
    public ResponseEntity viewAllVersion() {
        return new ResponseEntity(adminClientService.getAllVersionList(), HttpStatus.OK);
    }

    @GetMapping("getFull")
    public ResponseEntity viewFullVersion(@RequestParam("full") final String inputUrl) {
        try {
            return new ResponseEntity(adminClientService.getFullJsonContent(inputUrl, JsonType.FULL), HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity(DefaultRes.FAIL_DEFAULT_RES, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("getPatch")
    public ResponseEntity viewPatchVersion(@RequestParam("patch") final String inputUrl) {
        try {
            return new ResponseEntity(adminClientService.getFullJsonContent(inputUrl, JsonType.PATCH), HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity(DefaultRes.FAIL_DEFAULT_RES, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

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
