package com.masta.auth.membership.controller;

import com.masta.auth.membership.entity.AccountUser;
import com.masta.auth.membership.entity.GuestUser;
import com.masta.auth.membership.entity.SocialUser;
import com.masta.auth.membership.entity.User;
import com.masta.auth.membership.repository.AccountUserRepository;
import com.masta.auth.membership.repository.GuestUserRepository;
import com.masta.auth.membership.repository.SocialUserRepository;
import com.masta.auth.membership.repository.UserRepository;
import com.masta.core.response.DefaultRes;
import com.masta.core.response.ResponseMessage;
import com.masta.core.response.StatusCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.xml.ws.Response;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * 관리자  api (오직 관리자만이 접근 가능한 api)
 * ++ 유저 전체 리스트 조회
 * ++ 유저에서 관리자로 승급
 */
@RestController
@Slf4j
@RequestMapping("/admin")
public class AdminController {

//    @Autowired
//    UserRepository userRepository;

    @Autowired
    SocialUserRepository socialUserRepository;

    @Autowired
    AccountUserRepository accountUserRepository;

    @Autowired
    GuestUserRepository guestUserRepository;

    /**
     * 유저 전체 리스트 조회
     * User Entity를 가져옴
     *
     * @param model
     * @return
     */
//    @GetMapping("/users")
//    public ResponseEntity userList(Model model) {
//        List<SocialUser> userList = socialUserRepository.findAll();
//        userList.add(socialUserRepository.findAll());
//        log.info(userList.toString());
//        if(!accountUserRepository.findAll().isEmpty()) {
//            userList.add(accountUserRepository.findAll());
//        }
//        if(!guestUserRepository.findAll().isEmpty()) {
//            userList.add(guestUserRepository.findAll());
//        }
//
//        return new ResponseEntity(userList.toString(), HttpStatus.OK);
//    }
    @GetMapping("/users")
    public ResponseEntity userList(Model model) {
        List<List> userList = new ArrayList<>();

        // List<List>로 Social, Account, Guest를 가져옴. 없으면 빈 리스트 ([])를 가져오지 않고 PASS
        userList.add(socialUserRepository.findAll());
        if (!accountUserRepository.findAll().isEmpty())
            userList.add(accountUserRepository.findAll());
        if (!guestUserRepository.findAll().isEmpty())
            userList.add(guestUserRepository.findAll());

        return new ResponseEntity(userList.toString(), HttpStatus.OK);

    }
}
