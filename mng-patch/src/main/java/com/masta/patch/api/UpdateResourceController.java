package com.masta.patch.api;

import com.masta.patch.service.UpdateService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.masta.core.response.DefaultRes.FAIL_DEFAULT_RES;

@Slf4j
@RequestMapping("updateResource")
@RestController
public class UpdateResourceController {
    final UpdateService updateService;


    public UpdateResourceController(final UpdateService updateService) {
        this.updateService = updateService;
    }

    @PostMapping("Merge")
    public ResponseEntity updateClientResource(@RequestBody final String clientVersion){
        try {
            return new ResponseEntity(updateService.updateNewVersion(clientVersion), HttpStatus.OK);
        } catch (Exception e) {
            log.error(e.getMessage());
            return new ResponseEntity<>(FAIL_DEFAULT_RES, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
