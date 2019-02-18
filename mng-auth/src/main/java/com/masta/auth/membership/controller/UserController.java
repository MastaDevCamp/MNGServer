package com.masta.auth.membership.controller;

import com.masta.auth.exception.exceptions.NoSuchDataException;
import com.masta.auth.jwt.JwtTokenProvider;
import com.masta.auth.membership.dto.GuestUserForm;
import com.masta.auth.membership.dto.LoginRes;
import com.masta.auth.membership.dto.AccountUserForm;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;

import static com.masta.core.response.DefaultRes.FAIL_DEFAULT_RES;

/**
 * 소셜, 게스트, 계정 유저 모두를 위한 api
 * 게스트, 계정 회원가입 및 로그인
 * ++ 유저 탈퇴
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
        this.userService=userService;
        this.guestUserService = guestUserService;
        this.accountUserService = accountUserService;
        this.authenticationManager = authenticationManager;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    /**
     * 계정을 생성하여  회원가입 기능입니다.
     * @param accountUserForm
     * @return  if success HttpStatus.OK return
     */
    @PostMapping("/join/account")
    public ResponseEntity joinAccount(@RequestBody AccountUserForm accountUserForm) {
        try {
            AccountUser newUser = accountUserService.getUser(accountUserForm.getUsername());
            if (newUser != null) {
                return new ResponseEntity(DefaultRes.res(StatusCode.OK, ResponseMessage.EXIST_USER), HttpStatus.OK);
            }
            // ++ 이메일 인증했는지 확인
            accountUserService.createUser(accountUserForm.toNonSocailUser());
            return new ResponseEntity("success", HttpStatus.OK);
        }catch (Exception e){
            log.error(e.getMessage());
            return new ResponseEntity<>(FAIL_DEFAULT_RES, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/join/guest")
    public ResponseEntity joinGuest(){
        try{
            GuestUser guestUser=guestUserService.saveGuestUser(); // ++ maybe return guest_id;
            return new ResponseEntity(guestUser.getGuestId(),HttpStatus.OK);
        }
        catch (Exception e){
            log.error(e.getMessage());
            return new ResponseEntity<>(FAIL_DEFAULT_RES, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * account login function.
     * @param accountUserForm(String username, String password)
     * @return if login success return LoginRes(String username, Long userNum)
     */
    @PostMapping("/login/account")
    public ResponseEntity loginAccount(@RequestBody AccountUserForm accountUserForm){
           try{
               Authentication authentication = authenticationManager.authenticate(
                       new UsernamePasswordAuthenticationToken(accountUserForm.getUsername(), accountUserForm.getPassword()));
               AccountUser accountUser = accountUserService.getUser(accountUserForm.getUsername());
               LoginRes loginRes = createJwtToken(accountUser);
               return new ResponseEntity(loginRes, HttpStatus.OK);
           }catch(AuthenticationException e){
                log.error(e.getMessage());
               return new ResponseEntity(DefaultRes.res(StatusCode.OK, ResponseMessage.INVALID_USER_DATA), HttpStatus.OK);
           }
           catch (Exception e){
               log.error(e.getMessage());
               return new ResponseEntity<>(FAIL_DEFAULT_RES, HttpStatus.INTERNAL_SERVER_ERROR);
           }
    }

    @PostMapping("/login/guest")
    public ResponseEntity LoginGuest(@RequestBody GuestUserForm guestUserForm){
        try{
            GuestUser guestUser = guestUserService.findGuestUser(guestUserForm.getGuest_id());
            LoginRes loginRes = createJwtToken(guestUser);
            return new ResponseEntity(loginRes, HttpStatus.OK);
        }
        catch (NoSuchDataException e){
            return new ResponseEntity(DefaultRes.res(StatusCode.OK, ResponseMessage.INVALID_USER_DATA), HttpStatus.OK);
        }
        catch (Exception e){
            log.error(e.getMessage());
            return new ResponseEntity<>(FAIL_DEFAULT_RES, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/user/{usernum}")
    public ResponseEntity DeleteUser(@PathVariable Long usernum){
        try{
            userService.DeleteUser(usernum);
            return new ResponseEntity("Delete Success", HttpStatus.OK);
        } catch (NoSuchDataException e){
            return new ResponseEntity(DefaultRes.res(StatusCode.OK, ResponseMessage.INVALID_USER_DATA), HttpStatus.OK);
        }catch (Exception e){
            log.error(e.getMessage());
            return new ResponseEntity<>(FAIL_DEFAULT_RES, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * 로그인 과정에서 jwt를 발급하는 method
     * @param user
     * @return  LoginRes(로그인 한 후 결과 값)
     */
        public LoginRes createJwtToken(User user) {
            String token = jwtTokenProvider.createToken(user.getNum(), user.getAuthority());
            return  LoginRes.builder()
                    .userNum(user.getNum())
                    .token(token)
                    .build();
        }


}