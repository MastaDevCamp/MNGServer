package com.masta.auth.membership.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 관리자  api (오직 관리자만이 접근 가능한 api)
 * ++ 유저 전체 리스트 조회
 * ++ 유저에서 관리자로 승급
 */
@RestController
@RequestMapping("/admin")
public class AdminController {
}
