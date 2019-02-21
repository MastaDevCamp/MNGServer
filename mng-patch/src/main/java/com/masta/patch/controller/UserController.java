package com.masta.patch.controller;

import com.masta.core.response.DefaultRes;
import com.masta.core.response.ResponseMessage;
import com.masta.core.response.StatusCode;
import com.masta.patch.service.UpdateService;
import com.masta.patch.service.UpdatettService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import static com.masta.core.response.DefaultRes.FAIL_DEFAULT_RES;

@AllArgsConstructor
@Slf4j
@RequestMapping("updateResource")
@RestController
public class UserController {

    private UpdatettService updatettService;
    private UpdateService updateService;

    @PostMapping("Merge")
    public ResponseEntity updateClientResource(@RequestBody final String clientVersion) {
        try {
            return new ResponseEntity(updatettService.updateNewVersion(clientVersion), HttpStatus.OK);
        } catch (Exception e) {
            log.error(e.getMessage());
            return new ResponseEntity<>(FAIL_DEFAULT_RES, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Transactional
    @GetMapping("lastVersion")
    public ResponseEntity checkLastVersion() {
        try {
            if (updateService.getLatestVersion() != null) {
                return new ResponseEntity<>(updateService.getLatestVersion(), HttpStatus.OK);
            }
            return new ResponseEntity(DefaultRes.res(StatusCode.OK, ResponseMessage.FILA_TO_GET_VERSION), HttpStatus.OK);
        } catch (Exception e) {
            log.error(e.getMessage());
            return new ResponseEntity<>(FAIL_DEFAULT_RES, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
