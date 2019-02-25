package com.masta.cms.api;

import com.masta.cms.auth.dto.UserDto;
import com.masta.cms.auth.jwt.JwtTokenProvider;
import com.masta.cms.model.MoneyReq;
import com.masta.cms.model.UserNickReq;
import com.masta.cms.model.UserReq;
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
    @GetMapping("/")
    public ResponseEntity getUserDetail(@RequestHeader(value="Authentiation") String authentication) {
        UserDto userDto = jwtTokenProvider.getUser(authentication, "ROLE_USER");
        try {
            Long usernum = userDto.getUsernum();
            return new ResponseEntity<>(userDetailService.getUserDetailWithNum(usernum), HttpStatus.OK);
        } catch (Exception e) {
            log.error(e.getMessage());
            return new ResponseEntity<>(FAIL_DEFAULT_RES, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @PutMapping("/nick")
    public ResponseEntity updateUserInfo(@RequestHeader(value="Authentiation") String authentication,
                                         @RequestBody final UserNickReq userNickReq) {
        UserDto userDto = jwtTokenProvider.getUser(authentication, "ROLE_USER");
        Long usernum = userDto.getUsernum();
        return new ResponseEntity<>(userDetailService.updateUserNickname(userNickReq.getNickname(), usernum), HttpStatus.OK);
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
    @PutMapping("/push")
    public ResponseEntity updateUserPush(@RequestHeader(value="Authentiation") String authentication) {
        UserDto userDto = jwtTokenProvider.getUser(authentication, "ROLE_USER");
        try {
            Long usernum = userDto.getUsernum();
            return new ResponseEntity<>(userDetailService.updateUserPushOnoff(usernum), HttpStatus.OK);
        } catch (Exception e) {
            log.error(e.getMessage());
            return new ResponseEntity<>(FAIL_DEFAULT_RES, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    //유저 재화 정보 수정
    @PutMapping("/good")
    public ResponseEntity updateUserMoneyInfo(@RequestHeader(value="Authentiation") String authentication,
                                              @RequestBody final MoneyReq money) {
        UserDto userDto = jwtTokenProvider.getUser(authentication, "ROLE_USER");
        try {
            Long usernum = userDto.getUsernum();
            log.info("user_num " + usernum);
            return new ResponseEntity<>(userDetailService.updateUserMoney(money.getRuby(), money.getGold(), usernum), HttpStatus.OK);
        } catch (Exception e) {
            log.error(e.getMessage());
            return new ResponseEntity<>(FAIL_DEFAULT_RES, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    //유저 하트 -1로 수정 (게임 실행시 호출되는 controller)
    @PutMapping("/heart/spend")
    public ResponseEntity spendUserHeartInfo(@RequestHeader(value="Authentiation") String authentication) {
        UserDto userDto = jwtTokenProvider.getUser(authentication, "ROLE_USER");
        try {
            Long usernum = userDto.getUsernum();
            return new ResponseEntity<>(userDetailService.spendOneHeart(usernum), HttpStatus.OK);
        } catch (Exception e) {
            log.error(e.getMessage());
            return new ResponseEntity<>(FAIL_DEFAULT_RES, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


}
