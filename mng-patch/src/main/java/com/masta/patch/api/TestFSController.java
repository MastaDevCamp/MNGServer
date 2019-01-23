package com.masta.patch.api;


import com.masta.patch.utils.FileSystem.FileSystem;
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

    @GetMapping("")
    public ResponseEntity getFileList(@RequestParam("path") final Optional<String> path) {
        try {
            return new ResponseEntity<>(fileSystem.getFileTreeList(path.get()), HttpStatus.OK);
        } catch (Exception e) {
            log.error(e.getMessage());
            return new ResponseEntity<>(FAIL_DEFAULT_RES, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
