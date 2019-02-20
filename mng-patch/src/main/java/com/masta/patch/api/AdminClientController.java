package com.masta.patch.api;

import com.masta.core.response.DefaultRes;
import com.masta.patch.model.JsonType;
import com.masta.patch.service.AdminClientService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@RequestMapping("admin")
@RestController
public class AdminClientController {

    private AdminClientService adminClientService;

    public AdminClientController(final AdminClientService adminClientService) {
        this.adminClientService = adminClientService;
    }

    /**
     * client!
     * <p>
     * 뭔가 controller 네이밍이 더 필요할 것 같음!!
     */
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
}
