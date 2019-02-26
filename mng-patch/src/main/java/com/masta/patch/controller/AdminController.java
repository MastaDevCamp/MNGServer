package com.masta.patch.controller;

import com.masta.core.response.DefaultRes;
import com.masta.patch.model.JsonType;
import com.masta.patch.model.PatchJson;
import com.masta.patch.service.AdminClientService;
import com.masta.patch.service.UpdateService;
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
    private final UpdateService updateService;
    private final PatchJson patchJson;

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
    public ResponseEntity uploadNewVersion(@RequestPart final MultipartFile newVersionFile, @RequestParam("newVersion") final String newVersion) {
        try {
            return new ResponseEntity(updateService.updateVersion(newVersionFile, newVersion), HttpStatus.OK);
        } catch (Exception e) {
            log.error(e.getMessage());
            return new ResponseEntity<>(FAIL_DEFAULT_RES, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("init")
    public ResponseEntity viewPatchVersion() {
        try {
            return new ResponseEntity(patchJson.getPatchJson(), HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity(DefaultRes.FAIL_DEFAULT_RES, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
