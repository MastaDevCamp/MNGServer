package com.masta.patch.api;

import com.masta.core.response.DefaultRes;
import com.masta.core.response.ResponseMessage;
import com.masta.core.response.StatusCode;
import com.masta.patch.utils.sftp.SftpServer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static com.masta.core.response.DefaultRes.FAIL_DEFAULT_RES;

@RestController
@Slf4j
public class TestController {

    private SftpServer sftpServer;

    public TestController(final SftpServer sftpServer) {
        this.sftpServer = sftpServer;
    }

    @PostMapping("test")
    public ResponseEntity updateClientResource() {
        try {
            sftpServer.init();
            sftpServer.downloadDir("log/patch");
            return new ResponseEntity<>(DefaultRes.res(StatusCode.OK, ResponseMessage.SUCCESS_TO_NEW_VERSION), HttpStatus.OK);
        } catch (Exception e) {
            log.error(e.getMessage());
            return new ResponseEntity<>(FAIL_DEFAULT_RES, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}

