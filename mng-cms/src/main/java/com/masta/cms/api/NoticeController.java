package com.masta.cms.api;

import com.masta.cms.auth.jwt.JwtTokenProvider;
import com.masta.cms.model.NoticeReq;
import com.masta.cms.service.NoticeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.context.ApplicationPidFileWriter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@CrossOrigin(origins="*")
@RestController
@RequestMapping("/notice")
public class NoticeController {
    private final NoticeService noticeService;
    private final JwtTokenProvider jwtTokenProvider;

    public NoticeController(NoticeService noticeService, JwtTokenProvider jwtTokenProvider) {
        this.noticeService = noticeService;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @GetMapping("")
    public ResponseEntity getAllNotice(@RequestHeader(value="Authentiation") String authentication){
        jwtTokenProvider.getUser(authentication, "ROLE_USER");
        return new ResponseEntity<>(noticeService.getNotice(), HttpStatus.OK);
    }

    //공지사항 타입별 보기
    @GetMapping("/type/{type}")
    public ResponseEntity getAllNoticeWithType(@RequestHeader(value="Authentiation") String authentication,
                                               @PathVariable final int type) {
        jwtTokenProvider.getUser(authentication, "ROLE_USER");
        return new ResponseEntity<>(noticeService.getNoticeByType(type), HttpStatus.OK);
    }

    //공지사항
    @GetMapping("/{notice_id}")
    public ResponseEntity getOneNotice(@RequestHeader(value="Authentiation") String authentication,
                                       @PathVariable final int notice_id) {
        jwtTokenProvider.getUser(authentication, "ROLE_USER");
        return new ResponseEntity<>(noticeService.getOneNoticeById(notice_id), HttpStatus.OK);
    }


    //공지사항 등록
    @PostMapping("")
    public ResponseEntity postNotice(@RequestHeader(value="Authentiation") String authentication,
                                     @RequestBody final NoticeReq noticeReq){
        jwtTokenProvider.getUser(authentication, "ROLE_USER");
        return new ResponseEntity<>(noticeService.postNotice(noticeReq), HttpStatus.OK);
    }

    //공지사항 수정
    @PutMapping("/{notice_id}")
    public ResponseEntity updateNotice(@RequestHeader(value="Authentiation") String authentication,
                                       @PathVariable final int notice_id,
                                       @RequestBody final NoticeReq noticeReq) {
        jwtTokenProvider.getUser(authentication, "ROLE_USER");
        return new ResponseEntity<>(noticeService.updateNoticeById(noticeReq, notice_id), HttpStatus.OK);
    }

    //공지사항 삭제
    @DeleteMapping("/{notice_id}")
    public ResponseEntity deleteNotie(@RequestHeader(value="Authentiation") String authentication,
                                      @PathVariable final int notice_id) {
        jwtTokenProvider.getUser(authentication, "ROLE_USER");
        return new ResponseEntity<>(noticeService.deleteNoticeById(notice_id), HttpStatus.OK);
    }


    @GetMapping("/check-period")
    public ResponseEntity checkNoticeValidPeriod() {

        return new ResponseEntity<>(noticeService.checkNoticePeriod(), HttpStatus.OK);
    }
}
