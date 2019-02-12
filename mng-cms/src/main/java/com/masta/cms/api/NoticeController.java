package com.masta.cms.api;

import com.masta.cms.model.NoticeReq;
import com.masta.cms.service.NoticeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/notice")
public class NoticeController {
    private final NoticeService noticeService;

    private NoticeController(final NoticeService noticeService) {
        this.noticeService = noticeService;
    }

    //공지사항 타입별 보기
    @GetMapping("")
    public ResponseEntity getAllNotice(@RequestParam("type") final int type) {
        return new ResponseEntity<>(noticeService.getNoticeByType(type), HttpStatus.OK);
    }

    //공지사항
    @GetMapping("/{notice_id}")
    public ResponseEntity getOneNotice(@PathVariable final int notice_id) {
        return new ResponseEntity<>(noticeService.getOneNoticeById(notice_id), HttpStatus.OK);
    }


    //공지사항 등록
    @PostMapping("")
    public ResponseEntity postNotice(@RequestBody final NoticeReq noticeReq){
        return new ResponseEntity<>(noticeService.postNotice(noticeReq), HttpStatus.OK);
    }

    //공지사항 수정
    @PutMapping("/{notice_id}")
    public ResponseEntity updateNotice(@PathVariable final int notice_id,
                                       @RequestBody final NoticeReq noticeReq) {
        return new ResponseEntity<>(noticeService.updateNoticeById(noticeReq, notice_id), HttpStatus.OK);
    }

    //공지사항 삭제
    @DeleteMapping("/{notice_id}")
    public ResponseEntity deleteNotie(@PathVariable final int notice_id) {
        return new ResponseEntity<>(noticeService.deleteNoticeById(notice_id), HttpStatus.OK);
    }
}
