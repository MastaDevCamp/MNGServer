package com.masta.auth.membership.controller;

import com.masta.auth.exception.exceptions.NoSuchDataException;
import com.masta.auth.jwt.JwtTokenProvider;
import com.masta.auth.membership.dto.AccountUserForm;
import com.masta.auth.membership.dto.GuestUserForm;
import com.masta.auth.membership.dto.LoginRes;
import com.masta.auth.membership.entity.AccountUser;
import com.masta.auth.membership.entity.GuestUser;
import com.masta.auth.membership.entity.User;
import com.masta.auth.membership.service.AccountUserService;
import com.masta.auth.membership.service.GuestUserService;
import com.masta.auth.membership.service.UserService;
import com.masta.core.response.DefaultRes;
import com.masta.core.response.ResponseMessage;
import com.masta.core.response.StatusCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.http.*;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import static com.masta.core.response.DefaultRes.FAIL_DEFAULT_RES;

/**
 * 소셜, 게스트, 계정 유저 모두를 위한 controller
 * - 게스트, 계정 회원가입 및 로그인
 * - 유저 탈퇴
 * ++ 로그아웃
 */
@Slf4j
@RestController
public class UserController {

    final UserService userService;
    final GuestUserService guestUserService;
    final AccountUserService accountUserService;
    final AuthenticationManager authenticationManager;
    final JwtTokenProvider jwtTokenProvider;

    public UserController(UserService userService, GuestUserService guestUserService, AccountUserService accountUserService,
                          AuthenticationManager authenticationManager, JwtTokenProvider jwtTokenProvider) {
        this.userService = userService;
        this.guestUserService = guestUserService;
        this.accountUserService = accountUserService;
        this.authenticationManager = authenticationManager;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    /**
     * 계정을 생성하여  회원가입 기능입니다.
     *
     * @param accountUserForm
     * @return if success HttpStatus.OK return
     */
    @PostMapping("/join/account")
    public ResponseEntity joinAccount(@RequestBody AccountUserForm accountUserForm) {
        try {
            if (hasUsername(accountUserForm.getUsername())) {
                return new ResponseEntity(DefaultRes.res(StatusCode.OK, ResponseMessage.EXIST_USER), HttpStatus.OK);
            }
            // ++ 이메일 인증했는지 확인
            User user = accountUserService.createUser(accountUserForm.toAccountUser());
            RestTemplate restTemplate = new RestTemplate();
            JSONObject req = new JSONObject();
            req.put("usernum",user.getNum());
            System.out.println(user.getNum());
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<String> entity = new HttpEntity<>(req.toString(),headers);
            restTemplate.exchange("http://175.210.61.143:8201/init", HttpMethod.POST,entity,HttpEntity.class);
            return new ResponseEntity("success", HttpStatus.OK);
        } catch (Exception e) {
            log.error(e.getMessage());
            return new ResponseEntity<>(FAIL_DEFAULT_RES, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/join/guest")
    public ResponseEntity joinGuest() {
        try {
            GuestUser guestUser = guestUserService.saveGuestUser(); // ++ maybe return guest_id;
            RestTemplate restTemplate = new RestTemplate();
            JSONObject req = new JSONObject();
            req.put("usernum",guestUser.getNum());
            System.out.println(guestUser.getNum());
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<String> entity = new HttpEntity<>(req.toString(),headers);
            restTemplate.exchange("http://175.210.61.143:8201/init", HttpMethod.POST,entity,HttpEntity.class);
            return new ResponseEntity(guestUser.getGuestId(), HttpStatus.OK);
        } catch (Exception e) {
            log.error(e.getMessage());
            return new ResponseEntity<>(FAIL_DEFAULT_RES, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * account login function.
     *
     * @param accountUserForm(String username,String password)
     * @return if login success return LoginRes(String username, Long userNum)
     */
    @PostMapping("/login/account")
    public ResponseEntity loginAccount(@RequestBody AccountUserForm accountUserForm) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(accountUserForm.getUsername(), accountUserForm.getPassword()));
            AccountUser accountUser = accountUserService.getUser(accountUserForm.getUsername());
            String token = "Bearer "+ createJwtToken(accountUser);
            LoginRes loginRes = createJwtToken(accountUser);
            return new ResponseEntity(loginRes, HttpStatus.OK);
        } catch (AuthenticationException e) {
            log.error(e.getMessage());
            return new ResponseEntity(DefaultRes.res(StatusCode.OK, ResponseMessage.INVALID_USER_DATA), HttpStatus.OK);
        } catch (Exception e) {
            log.error(e.getMessage());
            return new ResponseEntity<>(FAIL_DEFAULT_RES, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/login/guest")
    public ResponseEntity LoginGuest(@RequestBody GuestUserForm guestUserForm) {
        try {
            GuestUser guestUser = guestUserService.findGuestUser(guestUserForm.getGuest_id());
            LoginRes loginRes = createJwtToken(guestUser);
            return new ResponseEntity(loginRes, HttpStatus.OK);
        } catch (NoSuchDataException e) {
            return new ResponseEntity(DefaultRes.res(StatusCode.OK, ResponseMessage.INVALID_USER_DATA), HttpStatus.OK);
        } catch (Exception e) {
            log.error(e.getMessage());
            return new ResponseEntity<>(FAIL_DEFAULT_RES, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/user/{usernum}")
    public ResponseEntity DeleteUser(@PathVariable Long usernum) {
        try {
            userService.DeleteUser(usernum);
            return new ResponseEntity("Delete Success", HttpStatus.OK);
        } catch (NoSuchDataException e) {
            return new ResponseEntity(DefaultRes.res(StatusCode.OK, ResponseMessage.INVALID_USER_DATA), HttpStatus.OK);
        } catch (Exception e) {
            log.error(e.getMessage());
            return new ResponseEntity<>(FAIL_DEFAULT_RES, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

//    @PostMapping("/logout")
//    public ResponseEntity logout(Authentication authentication){
//
//        log.info(authentication.getAuthorities().toString());
//
//        return ResponseEntity.ok(HttpStatus.OK);
//
//    }

    /**
     * 로그인 과정에서 jwt를 발급하는 method
     *
     * @param user
     * @return LoginRes(로그인 한 후 결과 값)
     */
    public LoginRes createJwtToken(User user) {
        String token = "Bearer " + jwtTokenProvider.createToken(user.getNum(), user.getAuthority());
        return LoginRes.builder()
                .userNum(user.getNum())
                .token(token)
                .build();
    }

    /**
     * 중복확인 메서드
     * 아이디 있으면 true 반환.
     *
     * @param id
     * @return
     */
    public Boolean hasUsername(String id) {
        AccountUser user = accountUserService.getUser(id);
        if (user == null) return false;
        else return true;
    }

}
