package com.masta.cms.api;

import com.masta.cms.auth.jwt.JwtTokenProvider;
import com.masta.cms.model.MoneyReq;
import com.masta.cms.model.UserNickReq;
import com.masta.cms.service.UserDetailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.masta.core.response.DefaultRes.FAIL_DEFAULT_RES;

@Slf4j
@RestController
@RequestMapping("/detail")
public class UserDetailController {
    private final JwtTokenProvider jwtTokenProvider;
    private final UserDetailService userDetailService;

    public UserDetailController(JwtTokenProvider jwtTokenProvider, UserDetailService userDetailService) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.userDetailService = userDetailService;
    }



    //유저 디테일 정보 보기
    @GetMapping("/{uid}")
    public ResponseEntity getUserDetail(@RequestHeader(value="Authentiation") String authentication,
                                        @PathVariable final int uid) {
        jwtTokenProvider.getUser(authentication, "ROLE_USER");
        try {
            return new ResponseEntity<>(userDetailService.getUserDetailWithId(uid), HttpStatus.OK);
        } catch (Exception e) {
            log.error(e.getMessage());
            return new ResponseEntity<>(FAIL_DEFAULT_RES, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @PutMapping("/nick/{uid}")
    public ResponseEntity updateUserInfo(@RequestHeader(value="Authentiation") String authentication,
                                         @RequestBody final UserNickReq userNickReq,
                                         @PathVariable final int uid) {
        jwtTokenProvider.getUser(authentication, "ROLE_USER");
        return new ResponseEntity<>(userDetailService.updateUserNickname(userNickReq.getNickname(), uid), HttpStatus.OK);
    }


//    @PutMapping("/nick/{uid}")
//    public ResponseEntity updateUserNickname(@RequestBody final String nickname,
//                                             @PathVariable final int uid){
//        try {
//            log.info("nickname : " + nickname);
//            return new ResponseEntity<>(userDetailService.updateUserNickname(nickname, uid), HttpStatus.OK);
//        } catch (Exception e) {
//            log.error(e.getMessage());
//            return new ResponseEntity<>(FAIL_DEFAULT_RES, HttpStatus.INTERNAL_SERVER_ERROR);
//        }
//    }

    //유저 푸시 온오프
    @PutMapping("/push/{uid}")
    public ResponseEntity updateUserPush(@RequestHeader(value="Authentiation") String authentication,
                                         @PathVariable final int uid) {
        jwtTokenProvider.getUser(authentication, "ROLE_USER");
        try {
            return new ResponseEntity<>(userDetailService.updateUserPushOnoff(uid), HttpStatus.OK);
        } catch (Exception e) {
            log.error(e.getMessage());
            return new ResponseEntity<>(FAIL_DEFAULT_RES, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    //유저 재화 정보 수정
    @PutMapping("/good/{uid}")
    public ResponseEntity updateUserMoneyInfo(@RequestHeader(value="Authentiation") String authentication,
                                              @RequestBody final MoneyReq money,
                                              @PathVariable final int uid) {
        jwtTokenProvider.getUser(authentication, "ROLE_USER");
        try {
            log.info("user_id " + uid);
            return new ResponseEntity<>(userDetailService.updateUserMoney(money.getRuby(), money.getGold(), uid), HttpStatus.OK);
        } catch (Exception e) {
            log.error(e.getMessage());
            return new ResponseEntity<>(FAIL_DEFAULT_RES, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    //유저 하트 -1로 수정 (게임 실행시 호출되는 controller)
    @PutMapping("/heart/spend/{uid}")
    public ResponseEntity updateUserHeartInfo(@RequestHeader(value="Authentiation") String authentication,
                                              @PathVariable final int uid) {
        jwtTokenProvider.getUser(authentication, "ROLE_USER");
        try {
            return new ResponseEntity<>(userDetailService.spendOneHeart(uid), HttpStatus.OK);
        } catch (Exception e) {
            log.error(e.getMessage());
            return new ResponseEntity<>(FAIL_DEFAULT_RES, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
