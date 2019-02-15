package com.masta.auth.membership.controller;

import com.masta.auth.membership.dto.UserDTO;
import com.masta.auth.membership.entity.AccountUser;
import com.masta.auth.membership.entity.GuestUser;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.masta.core.response.DefaultRes.FAIL_DEFAULT_RES;

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

    /**
     * 나중에 page, sort 적용하기
     * 전제 유저 목록 가져옴
     * @return  List<UserDTO>
     */
    @GetMapping("/users")
    public ResponseEntity<List<UserDTO>> allUserList() {
        try {
            List<User> userList = userService.getUserList();
            List<UserDTO> userDTOList = new ArrayList<>();
            for(User user: userList){
                userDTOList.add(
                        UserDTO.builder()
                                .authority(user.getAuthority())
                                .usernum(user.getNum())
                                .type(user.getClass().getSimpleName())
                                .build()
                );
            }
            return new ResponseEntity(userDTOList, HttpStatus.OK);
        }
        catch (Exception e){
            log.error(e.getMessage());
            return new ResponseEntity(FAIL_DEFAULT_RES, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * 유저 1명 상세 정보 가져옴
     * User Entity를 가져옴
     * @param id
     * @return
     */
    @GetMapping("/users/{id}")
    public ResponseEntity<UserDTO> userList(@PathVariable final long id) {
        try {
            User user = userService.getUser(id);
            UserDTO userDTO= UserDTO.builder()
                    .authority(user.getAuthority())
                    .usernum(user.getNum())
                    .type(user.getClass().getSimpleName())
                    .build();
            return new ResponseEntity(userDTO, HttpStatus.OK);
        } catch (Exception e){
            log.error(e.getMessage());
            return new ResponseEntity(FAIL_DEFAULT_RES, HttpStatus.INTERNAL_SERVER_ERROR);
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
