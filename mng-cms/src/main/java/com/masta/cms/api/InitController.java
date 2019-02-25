package com.masta.cms.api;

import com.masta.cms.auth.dto.UserDto;
import com.masta.cms.auth.jwt.JwtTokenProvider;
import com.masta.cms.model.UserReq;
import com.masta.cms.model.UsernumReq;
import com.masta.cms.service.UserInitService;
import com.masta.core.response.DefaultRes;
import com.masta.core.response.StatusCode;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.User;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/init")
public class InitController {
    private final UserInitService userInitService;
    private final JwtTokenProvider jwtTokenProvider;

    public InitController(UserInitService userInitService, JwtTokenProvider jwtTokenProvider) {
        this.userInitService = userInitService;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @PostMapping("")
    public ResponseEntity initUser(@RequestBody final UsernumReq usernumReq) {
        log.info("사용자 넘 : " + usernumReq.getUsernum());
        return new ResponseEntity<>(userInitService.createNewUser(usernumReq.getUsernum()), HttpStatus.OK);
//        return new ResponseEntity<>(DefaultRes.res(StatusCode.OK, "OK"), HttpStatus.OK);
    }
}
