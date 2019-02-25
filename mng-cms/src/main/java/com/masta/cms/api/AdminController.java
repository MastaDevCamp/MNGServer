package com.masta.cms.api;

import com.masta.cms.auth.jwt.JwtTokenProvider;
import com.masta.cms.model.MoneyReq;
import com.masta.cms.model.NoticeReq;
import com.masta.cms.model.PartnerReq;
import com.masta.cms.model.UserReq;
import com.masta.cms.service.NoticeService;
import com.masta.cms.service.PartnerService;
import com.masta.cms.service.UserDetailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.masta.core.response.DefaultRes.FAIL_DEFAULT_RES;

@Slf4j
@CrossOrigin(origins="*")
@RestController
@RequestMapping("/admin")
public class AdminController {
    private final NoticeService noticeService;
    private final UserDetailService userDetailService;
    private final JwtTokenProvider jwtTokenProvider;
    private final PartnerService partnerService;

    public AdminController(NoticeService noticeService, UserDetailService userDetailService, JwtTokenProvider jwtTokenProvider, PartnerService partnerService) {
        this.noticeService = noticeService;
        this.userDetailService = userDetailService;
        this.jwtTokenProvider = jwtTokenProvider;
        this.partnerService = partnerService;
    }

    @GetMapping("/notice")
    public ResponseEntity getAllNotice(@RequestHeader(value="Authentiation") String authentication) {
        jwtTokenProvider.getUser(authentication, "ROLE_ADMIN");
        return new ResponseEntity<>(noticeService.getNotice(), HttpStatus.OK);
    }

    //공지사항 타입별 보기
    @GetMapping("/notice/type/{type}")
    public ResponseEntity getAllNoticeWithType(@RequestHeader(value="Authentiation") String authentication,
                                               @PathVariable final int type) {
        jwtTokenProvider.getUser(authentication, "ROLE_ADMIN");
        return new ResponseEntity<>(noticeService.getNoticeByType(type), HttpStatus.OK);
    }

    //공지사항
    @GetMapping("/notice/{notice_id}")
    public ResponseEntity getOneNotice(@RequestHeader(value="Authentiation") String authentication,
                                       @PathVariable final int notice_id) {
        jwtTokenProvider.getUser(authentication, "ROLE_ADMIN");
        return new ResponseEntity<>(noticeService.getOneNoticeById(notice_id), HttpStatus.OK);
    }

    //공지사항 수정
    @PutMapping("/notice/{notice_id}")
    public ResponseEntity updateNotice(@RequestHeader(value="Authentiation") String authentication,
                                       @PathVariable final int notice_id,
                                       @RequestBody final NoticeReq noticeReq) {
        jwtTokenProvider.getUser(authentication, "ROLE_ADMIN");
        return new ResponseEntity<>(noticeService.updateNoticeById(noticeReq, notice_id), HttpStatus.OK);
    }

    //공지사항 삭제
    @DeleteMapping("/notice/{notice_id}")
    public ResponseEntity deleteNotie(@RequestHeader(value="Authentiation") String authentication,
                                      @PathVariable final int notice_id) {
        jwtTokenProvider.getUser(authentication, "ROLE_ADMIN");
        return new ResponseEntity<>(noticeService.deleteNoticeById(notice_id), HttpStatus.OK);
    }


    @GetMapping("/notice/check-period")
    public ResponseEntity checkNoticeValidPeriod(@RequestHeader(value="Authentiation") String authentication) {
        jwtTokenProvider.getUser(authentication, "ROLE_ADMIN");
        return new ResponseEntity<>(noticeService.checkNoticePeriod(), HttpStatus.OK);
    }

    /********** User Game Detail Info ************/
    @GetMapping("/users")
    public ResponseEntity getAllUserDetail(@RequestHeader(value="Authentiation") String authentication) {
        jwtTokenProvider.getUser(authentication, "ROLE_ADMIN");
        return new ResponseEntity<>(userDetailService.getAllUserDetail(), HttpStatus.OK);
    }

    @GetMapping("/users/{uid}")
    public ResponseEntity getOneUserDetail(@RequestHeader(value="Authentiation") String authentication,
                                           @PathVariable int uid) {
        jwtTokenProvider.getUser(authentication, "ROLE_ADMIN");
        return new ResponseEntity<>(userDetailService.getUserDetailWithId(uid), HttpStatus.OK);
    }

    @PutMapping("/users/{user_id}")
    public ResponseEntity modifyUserDetail(@RequestHeader(value="Authentiation") String authentication,
                                           @RequestBody UserReq userReq,
                                           @PathVariable int user_id) {
        jwtTokenProvider.getUser(authentication, "ROLE_ADMIN");
        try {
            return new ResponseEntity<>(userDetailService.updateAllInfo(user_id, userReq), HttpStatus.OK);
        } catch (Exception e) {
            log.error(e.getMessage());
            return new ResponseEntity<>(FAIL_DEFAULT_RES, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /********** User Default Info Setting ************/
    @GetMapping("/default/user")
    public ResponseEntity getDefaultUserInfo(@RequestHeader(value="Authentiation") String authentication) {
        jwtTokenProvider.getUser(authentication, "ROLE_ADMIN");
        return new ResponseEntity<>(userDetailService.getUserDetailWithId(0), HttpStatus.OK);
    }

    @GetMapping("/default/partner")
    public ResponseEntity getDefaultPartnerInfo(@RequestHeader(value="Authentiation") String authentication) {
        jwtTokenProvider.getUser(authentication, "ROLE_ADMIN");
        return new ResponseEntity<>(partnerService.getFavor(0), HttpStatus.OK);
    }

    @PutMapping("/default/user")
    public ResponseEntity modifyDefaultUserInfo(@RequestHeader(value="Authentiation") String authentication,
                                                @RequestBody UserReq userReq) {
        jwtTokenProvider.getUser(authentication, "ROLE_ADMIN");
        return new ResponseEntity<>(userDetailService.updateAllInfo(0, userReq), HttpStatus.OK);
    }

    @PutMapping("/default/partner")
    public ResponseEntity modifyDefaultPartnerInfo(@RequestHeader(value="Authentiation") String authentication,
                                                   @RequestBody PartnerReq partnerReq) {
        jwtTokenProvider.getUser(authentication, "ROLE_ADMIN");
        return new ResponseEntity<>(partnerService.editFavor(0, partnerReq.getPartner(), partnerReq.getLike(), partnerReq.getTrust()), HttpStatus.OK);
    }
}
