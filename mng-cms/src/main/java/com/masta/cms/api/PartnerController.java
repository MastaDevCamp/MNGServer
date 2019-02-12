package com.masta.cms.api;

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
    public PartnerController(PartnerService partnerService) { this.partnerService = partnerService; }

    @GetMapping("{uid}")
    public ResponseEntity getAllFavor(@PathVariable final int uid) {
        return new ResponseEntity<>(partnerService.getFavor(uid), HttpStatus.OK);
    }

    @PutMapping("{uid}")
    public ResponseEntity editFavor(@PathVariable final int uid,
                                    @RequestBody FavorReq favorReq) {
        return new ResponseEntity<>(partnerService.editFavor(uid, favorReq.getPartner(), favorReq.getLike(), favorReq.getTrust()), HttpStatus.OK);
    }
}
