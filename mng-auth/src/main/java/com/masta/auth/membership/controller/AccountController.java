package com.masta.auth.membership.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 유저 정보 조회 API
 * 유저 종류 : 소셜, 계정, 게스트
 *
 * 공통 부분 : 탈퇴, 로그인, 회원가입, 유저 정보 조회
 *
 * ==============================계정 로그인===============================
 * 1. 아이디 찾기 : 어떻게?
 * 2. 비밀번호 찾기 : 이메일로 비밀번호 임시값 보낸후 임시값 인증 후 변경 진행
 * 3. 비밀번호 변경
 * 3. 이메일 인증 : 계정 로그인 사람만 이메일 인증할수 있음. 이메일 있어야 아이디, 비밀번호 찾기 가능.
 *
 * ===========================게스트======================
 * 1. 계정 로그인으로 전환
 *
 */
@Slf4j
@RestController
@RequestMapping("/account")
public class AccountController {



}
