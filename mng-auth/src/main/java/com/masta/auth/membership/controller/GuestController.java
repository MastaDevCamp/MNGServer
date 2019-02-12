package com.masta.auth.membership.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 게스트 계정 관련 기능을 위한 API 입니다.
 * ++게스트 계정 일반 회원으로 전환
 */
@Slf4j
@RestController
@RequestMapping(value = "/guest")
public class GuestController {
}
