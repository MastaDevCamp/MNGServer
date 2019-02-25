package com.masta.cms.api;

import com.masta.cms.auth.dto.UserDto;
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

    @GetMapping("")
    public ResponseEntity getAllFavor(@RequestHeader(value="Authentiation") String authentication) {
        UserDto userDto = jwtTokenProvider.getUser(authentication, "ROLE_USER");
        Long usernum = userDto.getUsernum();
        return new ResponseEntity<>(partnerService.getFavor(usernum), HttpStatus.OK);
    }

    @PutMapping("")
    public ResponseEntity editFavor(@RequestHeader(value="Authentiation") String authentication,
                                    @RequestBody FavorReq favorReq) {
        UserDto userDto = jwtTokenProvider.getUser(authentication, "ROLE_USER");
        Long usernum = userDto.getUsernum();
        return new ResponseEntity<>(partnerService.editFavor(usernum, favorReq.getPartner(), favorReq.getLike(), favorReq.getTrust()), HttpStatus.OK);
    }
}
