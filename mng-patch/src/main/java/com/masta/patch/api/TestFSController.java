package com.masta.patch.api;


import com.masta.core.response.DefaultRes;
import com.masta.core.response.ResponseMessage;
import com.masta.core.response.StatusCode;
import com.masta.patch.utils.FileSystem.FileSystem;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

import static com.masta.core.response.DefaultRes.FAIL_DEFAULT_RES;


@Slf4j
@RestController
@RequestMapping("tfs")
public class TestFSController {

    private final FileSystem fileSystem;

    public TestFSController(final FileSystem fileSystem) {
        this.fileSystem = fileSystem;
    }


    @ApiOperation(value = "version json 생성")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "path", value = "Put path", required = true, dataType = "string", paramType = "query", defaultValue = ""),
    })
    @GetMapping("json")
    public ResponseEntity getDiffFileList(@RequestParam("path") final Optional<String> path) {
        try {
            if (path.isPresent()) {
                log.info("file to Json Converter");
                return new ResponseEntity<>(fileSystem.getFileTreeList(path.get()), HttpStatus.OK);
            }
            return new ResponseEntity(DefaultRes.res(StatusCode.OK, ResponseMessage.NOT_READ_JSON_FILE), HttpStatus.OK);

        } catch (Exception e) {
            log.error(e.getMessage());
            return new ResponseEntity<>(FAIL_DEFAULT_RES, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("patch")
    public ResponseEntity getPatchJson(@RequestParam("before") final Optional<String> before, @RequestParam("after") final Optional<String> after) {
        try {
            if (before.isPresent() && after.isPresent()) {
                log.info("full json to patch json (string list)");
                return new ResponseEntity<>(fileSystem.getPatchJson(before.get(),after.get()), HttpStatus.OK);
            }
            return new ResponseEntity(DefaultRes.res(StatusCode.OK, ResponseMessage.NOT_READ_JSON_FILE), HttpStatus.OK);

        } catch (Exception e) {
            log.error(e.getMessage());
            return new ResponseEntity<>(FAIL_DEFAULT_RES, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @GetMapping("list")
    public ResponseEntity getJson2List(@RequestParam("jsonPath") final Optional<String> jsonPath) {
        try {
            if (jsonPath.isPresent()) {
                log.info("converter json to list");
                return new ResponseEntity<>(fileSystem.makeFileList(jsonPath.get()), HttpStatus.OK);
            }
            return new ResponseEntity(DefaultRes.res(StatusCode.OK, ResponseMessage.NOT_READ_JSON_FILE), HttpStatus.OK);

        } catch (Exception e) {
            log.error(e.getMessage());
            return new ResponseEntity<>(FAIL_DEFAULT_RES, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


}
