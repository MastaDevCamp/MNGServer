package com.masta.core;

import com.masta.core.response.DefaultRes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.masta.core.response.ResponseMessage.READ_USER;

@RestController
public class TestController {

    private Logger logger = LoggerFactory.getLogger("test Controller logger");

    @GetMapping("/test")
    public DefaultRes test(){
        logger.info("테스트입니다.");
        return DefaultRes.res(200,READ_USER);
    }
}