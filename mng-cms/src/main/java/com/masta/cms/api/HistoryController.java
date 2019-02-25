package com.masta.cms.api;

import com.masta.cms.auth.dto.UserDto;
import com.masta.cms.auth.jwt.JwtTokenProvider;
import com.masta.cms.model.HistoryReq;
import com.masta.cms.service.HistoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/history")
public class HistoryController {
    private final HistoryService historyService;
    private final JwtTokenProvider jwtTokenProvider;

    public HistoryController(HistoryService historyService, JwtTokenProvider jwtTokenProvider) {
        this.historyService = historyService;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @GetMapping("/cg")
    public ResponseEntity getCGInfo(@RequestHeader(value = "Authentiation") String authentication) {
        UserDto userDto = jwtTokenProvider.getUser(authentication, "ROLE_USER");
        Long usernum = userDto.getUsernum();
        log.info("***************************usernum**************************" + usernum);
        return new ResponseEntity<>(historyService.getCGInfoWithNum(usernum), HttpStatus.OK);
    }

    @GetMapping("/ending")
    public ResponseEntity getEndingInfo(@RequestHeader(value = "Authentiation") String authentication) {
        UserDto userDto = jwtTokenProvider.getUser(authentication, "ROLE_USER");
        Long usernum = userDto.getUsernum();
        return new ResponseEntity<>(historyService.getEndingInfoWithNum(usernum), HttpStatus.OK);
    }

    @PostMapping("/cg")
    public ResponseEntity postCGinHistoryInfo(@RequestHeader(value = "Authentiation") String authentication,
                                              @RequestBody final HistoryReq historyReq) {
        UserDto userDto = jwtTokenProvider.getUser(authentication, "ROLE_USER");
        Long usernum = userDto.getUsernum();
        return new ResponseEntity<>(historyService.postCGinHistory(usernum, historyReq.getCg()), HttpStatus.OK);
    }

    @PostMapping("/ending")
    public ResponseEntity postEndingHistoryInfo(@RequestHeader(value = "Authentiation") String authentication,
                                                @RequestBody final HistoryReq historyReq) {
        UserDto userDto = jwtTokenProvider.getUser(authentication, "ROLE_USER");
        Long usernum = userDto.getUsernum();
        return new ResponseEntity<>(historyService.postEndinginHistory(usernum, historyReq.getEnding()), HttpStatus.OK);
    }

//    @PostMapping("/{uid}/ending")
//    public ResponseEntity postEndinginHistoryInfo(@PathVariable final int uid,
//                                                  @RequestBody final int ending) {
//        return new ResponseEntity<>(historyService.postEndinginHistory(uid, ending), HttpStatus.OK);
//    }
}
