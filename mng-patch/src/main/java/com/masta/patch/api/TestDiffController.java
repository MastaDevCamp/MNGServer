package com.masta.patch.api;

import com.masta.core.response.DefaultRes;
import com.masta.core.response.ResponseMessage;
import com.masta.core.response.StatusCode;
import com.masta.patch.utils.FileSystem.DiffSystem;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

import static com.masta.core.response.DefaultRes.FAIL_DEFAULT_RES;

@Slf4j
@RestController
public class TestDiffController {

    private final DiffSystem diffSystem;

    public TestDiffController(final DiffSystem diffSystem) {
        this.diffSystem = diffSystem;
    }


    @GetMapping("diff")
    public ResponseEntity getDiffFileList(@RequestParam("prev") final Optional<String> prevJson, @RequestParam("next") final Optional<String> nextJson) {
        try {
            if (prevJson.isPresent() && nextJson.isPresent()) {
                log.info("file to Json Converter");
                return new ResponseEntity<>(diffSystem.makePatchJson(prevJson.get(), nextJson.get()), HttpStatus.OK);
            }
            return new ResponseEntity(DefaultRes.res(StatusCode.OK, ResponseMessage.NOT_READ_JSON_FILE), HttpStatus.OK);

        } catch (Exception e) {
            log.error(e.getMessage());
            return new ResponseEntity<>(FAIL_DEFAULT_RES, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
