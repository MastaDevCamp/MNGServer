package com.masta.cms.api;

import com.masta.cms.dto.UserRewarded;
import com.masta.cms.model.MailboxReq;
import com.masta.cms.service.MailboxService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.masta.core.response.DefaultRes.FAIL_DEFAULT_RES;

@Slf4j
@RestController
@RequestMapping("/mailbox")
public class MailboxController {
    private final MailboxService mailboxService;

    private MailboxController(final MailboxService mailboxService) {
        this.mailboxService = mailboxService;
    }

//    @Autowired
//    MailboxService mailboxService;

    //메일박스 전체 조회
    @GetMapping("/{uid}")
    public ResponseEntity getAllMailbox(@PathVariable final int uid) {
        try {
            log.info("user_id : " + uid);
            return new ResponseEntity<>(mailboxService.getAllMailboxWithUid(uid), HttpStatus.OK);
        } catch (Exception e){
            log.error(e.getMessage());
            return new ResponseEntity<>(FAIL_DEFAULT_RES, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    //메일박스 하나 조회
    @GetMapping("/mail/{mailbox_id}")
    public ResponseEntity getOneMailbox(@PathVariable final int mailbox_id){
        try {
            return new ResponseEntity<>(mailboxService.getOneMailbox(mailbox_id), HttpStatus.OK);
        } catch (Exception e) {
            log.error(e.getMessage());
            return new ResponseEntity<>(FAIL_DEFAULT_RES, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    //메일박스 등록
    @PostMapping("/")
    public ResponseEntity postMailbox(@RequestBody final MailboxReq mailboxReq) {
        try {
            log.info("NoticeController");
            return new ResponseEntity<>(mailboxService.registerMail(mailboxReq), HttpStatus.OK);
        } catch (Exception e) {
            log.error(e.getMessage());
            return new ResponseEntity<>(FAIL_DEFAULT_RES, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    //특정 유저의 메일박스 조회 등록
    @PostMapping("/rewarded")
    public ResponseEntity postRewardedUser(@RequestBody UserRewarded userRewarded) {
        try {
//            final int uid = userRewarded.getUid();
//            final int mail_id = userRewarded.getMail_id();
//            log.info("uid : " + uid + " | mail_id : " + mail_id);

            return new ResponseEntity<>(mailboxService.postRewardedUser(userRewarded), HttpStatus.OK);
        } catch (Exception e){
            log.error(e.getMessage());
            return new ResponseEntity<>(FAIL_DEFAULT_RES, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/{mail_id}")
    public ResponseEntity putMailbox(@RequestBody final MailboxReq mailbox,
                                     @PathVariable final int mail_id) {
        return new ResponseEntity<>(mailboxService.modifyMail(mailbox, mail_id), HttpStatus.OK);
    }

    @DeleteMapping("/{mail_id}")
    public ResponseEntity deleteMailbox(@PathVariable final int mail_id) {
        return new ResponseEntity<>(mailboxService.deleteMail(mail_id), HttpStatus.OK);
    }
}
