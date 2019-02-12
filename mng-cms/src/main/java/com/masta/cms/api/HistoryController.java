package com.masta.cms.api;

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
    public HistoryController(HistoryService historyService) { this.historyService = historyService; }

    @GetMapping("/cg/{uid}")
    public ResponseEntity getCGInfo(@PathVariable final int uid) {
        return new ResponseEntity<>(historyService.getCGInfoWithUid(uid), HttpStatus.OK);
    }

    @GetMapping("/ending/{uid}")
    public ResponseEntity getEndingInfo(@PathVariable final int uid) {
        return new ResponseEntity<>(historyService.getEndingInfoWithUid(uid), HttpStatus.OK);
    }

    @PostMapping("/cg/{uid}")
    public ResponseEntity postCGinHistoryInfo(@PathVariable final int uid,
                                              @RequestBody final HistoryReq historyReq) {
        return new ResponseEntity<>(historyService.postCGinHistory(uid, historyReq.getCg()), HttpStatus.OK);
    }

    @PostMapping("/ending/{uid}")
    public ResponseEntity postEndingHistoryInfo(@PathVariable final int uid,
                                                @RequestBody final HistoryReq historyReq) {
        return new ResponseEntity<>(historyService.postEndinginHistory(uid, historyReq.getEnding()), HttpStatus.OK);
    }

//    @PostMapping("/{uid}/ending")
//    public ResponseEntity postEndinginHistoryInfo(@PathVariable final int uid,
//                                                  @RequestBody final int ending) {
//        return new ResponseEntity<>(historyService.postEndinginHistory(uid, ending), HttpStatus.OK);
//    }
}
