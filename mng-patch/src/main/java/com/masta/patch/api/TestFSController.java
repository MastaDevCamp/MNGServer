package com.masta.patch.api;


import com.fasterxml.jackson.annotation.JsonView;
import com.masta.core.response.DefaultRes;
import com.masta.core.response.ResponseMessage;
import com.masta.core.response.StatusCode;
import com.masta.patch.utils.FileSystem.FileSystem;
import com.masta.patch.utils.FileSystem.model.Views;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

import static com.masta.core.response.DefaultRes.FAIL_DEFAULT_RES;
import static com.masta.core.response.DefaultRes.res;

@Slf4j
@RestController
@RequestMapping("tfs")
public class TestFSController {

    private final FileSystem fileSystem;

    public TestFSController(final FileSystem fileSystem) {
        this.fileSystem = fileSystem;
    }

    @GetMapping("full")
    @JsonView(Views.Full.class)
    public ResponseEntity getFileList(@RequestParam("path") final Optional<String> path) {
        try {

            if(path.isPresent()){
                log.info("fullJsonController");
                return new ResponseEntity<>(fileSystem.getFileTreeList(path.get()), HttpStatus.OK);
            }
            return new ResponseEntity(DefaultRes.res(StatusCode.OK, ResponseMessage.NOT_READ_JSON_FILE), HttpStatus.OK);

        } catch (Exception e) {
            log.error(e.getMessage());
            return new ResponseEntity<>(FAIL_DEFAULT_RES, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("patch")
    @JsonView(Views.Patch.class)
    public ResponseEntity getDiffFileList(@RequestParam("path") final Optional<String> path) {
        try {

            if(path.isPresent()){
                log.info("patchJsonController");
                return new ResponseEntity<>(fileSystem.getFileTreeList(path.get()), HttpStatus.OK);
            }
            return new ResponseEntity(DefaultRes.res(StatusCode.OK, ResponseMessage.NOT_READ_JSON_FILE), HttpStatus.OK);

        } catch (Exception e) {
            log.error(e.getMessage());
            return new ResponseEntity<>(FAIL_DEFAULT_RES, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
