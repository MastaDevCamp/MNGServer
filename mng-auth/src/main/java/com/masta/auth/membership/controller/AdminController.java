package com.masta.auth.membership.controller;

import com.masta.auth.membership.entity.User;
import com.masta.auth.membership.service.AccountUserService;
import com.masta.auth.membership.service.GuestUserService;
import com.masta.auth.membership.service.SocialUserService;
import com.masta.auth.membership.service.UserService;
import com.masta.core.response.DefaultRes;
import com.masta.core.response.StatusCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.spring.web.json.Json;

import java.util.List;
import java.util.Optional;

/**
 * 관리자  api (오직 관리자만이 접근 가능한 api)
 * 유저 전체 리스트 조회
 * 단일 유저 조회
 * ++ 유저에서 관리자로 승급
 */
@RestController
@Slf4j
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    UserService userService;

    @Autowired
    SocialUserService socialUserService;

    @Autowired
    AccountUserService accountUserService;

    @Autowired
    GuestUserService guestUserService;


//    @GetMapping()
//    public ResponseEntity allUserList() {
//        List<List> userList = userService.getUserList();
//
//        return new ResponseEntity(userList.toString(), HttpStatus.OK);
//
//    }

    /**
     * 유저 전체 리스트 조회
     * User Entity를 가져옴
     * @param id
     * @return
     */
    @GetMapping({"/{id}", ""})
    public ResponseEntity userList(@PathVariable final Optional<Long> id) {
        if(id.isPresent()) {
            User user = userService.getUser(id.get());
            return new ResponseEntity(user.toString(), HttpStatus.OK);
        } else {
            List<List> userList = userService.getUserList();
            return new ResponseEntity(userList.toString(), HttpStatus.OK);
        }
    }

    /**
     * 권한 변경
     * @param id
     * @param role
     * @return
     */
    @PutMapping("/{id}")
    public ResponseEntity updateUser(@PathVariable final Long id, @RequestBody final String role) {
        String message = userService.updateUserRole(id, role);

        return new ResponseEntity(DefaultRes.res(StatusCode.OK, message), HttpStatus.OK);
    }

}
