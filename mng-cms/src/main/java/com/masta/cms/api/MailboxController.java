package com.masta.cms.api;

import com.masta.cms.auth.jwt.JwtTokenProvider;
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
    private final JwtTokenProvider jwtTokenProvider;

    public MailboxController(MailboxService mailboxService, JwtTokenProvider jwtTokenProvider) {
        this.mailboxService = mailboxService;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    //    @Autowired
//    MailboxService mailboxService;

    //메일박스 전체 조회
    @GetMapping("/{uid}")
    public ResponseEntity getAllMailbox(@RequestHeader(value="Authentiation") String authentication, @PathVariable final int uid) {
        jwtTokenProvider.getUser(authentication, "ROLE_USER");
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
    public ResponseEntity getOneMailbox(@RequestHeader(value="Authentiation") String authentication, @PathVariable final int mailbox_id){
        jwtTokenProvider.getUser(authentication, "ROLE_USER");
        try {
            return new ResponseEntity<>(mailboxService.getOneMailbox(mailbox_id), HttpStatus.OK);
        } catch (Exception e) {
            log.error(e.getMessage());
            return new ResponseEntity<>(FAIL_DEFAULT_RES, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    //메일박스 등록
    @PostMapping("/")
    public ResponseEntity postMailbox(@RequestHeader(value="Authentiation") String authentication, @RequestBody final MailboxReq mailboxReq) {
        jwtTokenProvider.getUser(authentication, "ROLE_USER");
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
    public ResponseEntity postRewardedUser(@RequestHeader(value="Authentiation") String authentication, @RequestBody UserRewarded userRewarded) {
        jwtTokenProvider.getUser(authentication, "ROLE_USER");
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
    public ResponseEntity putMailbox(@RequestHeader(value="Authentiation") String authentication, @RequestBody final MailboxReq mailbox,
                                     @PathVariable final int mail_id) {
        jwtTokenProvider.getUser(authentication, "ROLE_USER");
        return new ResponseEntity<>(mailboxService.modifyMail(mailbox, mail_id), HttpStatus.OK);
    }

    @DeleteMapping("/{mail_id}")
    public ResponseEntity deleteMailbox(@RequestHeader(value="Authentiation") String authentication, @PathVariable final int mail_id) {
        jwtTokenProvider.getUser(authentication, "ROLE_USER");
        return new ResponseEntity<>(mailboxService.deleteMail(mail_id), HttpStatus.OK);
    }
}
