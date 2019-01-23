package com.masta.auth;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/auth")
public class TestController {

    @GetMapping("/test")
    public String test(){
      log.info("test");
      return "hi";
    }
}
