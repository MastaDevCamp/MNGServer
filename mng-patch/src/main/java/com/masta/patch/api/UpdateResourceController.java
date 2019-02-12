package com.masta.patch.api;

import com.masta.core.response.DefaultRes;
import com.masta.core.response.ResponseMessage;
import com.masta.core.response.StatusCode;
import com.masta.patch.dto.VersionLog;
import com.masta.patch.mapper.VersionMapper;
import com.masta.patch.service.UpdateService;
import com.masta.patch.service.UploadService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import sun.misc.Version;

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
    public ResponseEntity updateClientResource(@RequestParam("version") final String clientVersion){
        try {
            ;
            return new ResponseEntity<>(DefaultRes.res(StatusCode.OK, ResponseMessage.UPDATE_NEW_VERSION(clientVersion),
                    updateService.updateNewVersion(clientVersion)), HttpStatus.OK);
        } catch (Exception e) {
            log.error(e.getMessage());
            return new ResponseEntity<>(FAIL_DEFAULT_RES, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
