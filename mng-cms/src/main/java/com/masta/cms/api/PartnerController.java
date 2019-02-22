package com.masta.cms.api;

import com.masta.cms.auth.jwt.JwtTokenProvider;
import com.masta.cms.model.FavorReq;
import com.masta.cms.service.PartnerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/favor")
public class PartnerController {
    private final PartnerService partnerService;
    private final JwtTokenProvider jwtTokenProvider;

    public PartnerController(PartnerService partnerService, JwtTokenProvider jwtTokenProvider) {
        this.partnerService = partnerService;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @GetMapping("{uid}")
    public ResponseEntity getAllFavor(@RequestHeader(value="Authentiation") String authentication,
                                      @PathVariable final int uid) {
        jwtTokenProvider.getUser(authentication, "ROLE_USER");
        return new ResponseEntity<>(partnerService.getFavor(uid), HttpStatus.OK);
    }

    @PutMapping("{uid}")
    public ResponseEntity editFavor(@RequestHeader(value="Authentiation") String authentication,
                                    @PathVariable final int uid,
                                    @RequestBody FavorReq favorReq) {
        jwtTokenProvider.getUser(authentication, "ROLE_USER");
        return new ResponseEntity<>(partnerService.editFavor(uid, favorReq.getPartner(), favorReq.getLike(), favorReq.getTrust()), HttpStatus.OK);
    }
}
