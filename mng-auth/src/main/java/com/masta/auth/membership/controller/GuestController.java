package com.masta.auth.membership.controller;

import com.masta.auth.exception.exceptions.NoSuchDataException;
import com.masta.auth.membership.dto.AccountUserForm;
import com.masta.auth.membership.entity.User;
import com.masta.auth.membership.service.AccountUserService;
import com.masta.auth.membership.service.GuestUserService;
import com.masta.auth.membership.service.UserService;
import com.masta.core.response.DefaultRes;
import com.masta.core.response.ResponseMessage;
import com.masta.core.response.StatusCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.masta.core.response.DefaultRes.FAIL_DEFAULT_RES;

/**
 * 게스트 계정 관련 기능을 위한 API 입니다.
 * - 게스트 계정 일반 회원으로 전환
 */
@Slf4j
@RestController
@RequestMapping(value = "/guest")
public class GuestController {

    @Autowired
    UserService userService;

    @Autowired
    GuestUserService guestUserService;

    @Autowired
    UserController userController;

    @Autowired
    AccountUserService accountUserService;


    @PutMapping("switch/{usernum}")
    public ResponseEntity switchUser(@PathVariable Long usernum, @RequestBody AccountUserForm accountUserForm){
        try{
            if(userController.hasUsername(accountUserForm.getUsername())){
                return new ResponseEntity(DefaultRes.res(StatusCode.OK, ResponseMessage.EXIST_USER), HttpStatus.OK);
            }
            accountUserService.switchUser(accountUserForm.toAccountUser(), usernum);
            return new ResponseEntity("Success",HttpStatus.OK);
        }catch (NoSuchDataException e){
            return new ResponseEntity(DefaultRes.res(StatusCode.OK, ResponseMessage.NOT_FOUND_USER), HttpStatus.OK);
        }
        catch (Exception e){
            log.error(e.getMessage());
            return new ResponseEntity<>(FAIL_DEFAULT_RES, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
