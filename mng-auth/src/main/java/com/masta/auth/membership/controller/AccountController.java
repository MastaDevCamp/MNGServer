package com.masta.auth.membership.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * non-social user 관련 기능을 위한 api 입니다.
 * ++ 아이디 찾기 : 어떻게?
 * ++ 비밀번호 찾기 : 이메일로 비밀번호 임시값 보낸후 임시값 인증 후 변경 진행
 * ++ 비밀번호 변경
 * ++ 이메일 인증하기 : 계정 로그인 사람만 이메일 인증할수 있음. 이메일 있어야 아이디, 비밀번호 찾기 가능.
 */
@Slf4j
@RestController
@RequestMapping("/account")
public class AccountController {

}
