package com.masta.cms.api;

import com.masta.cms.model.PushReq;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.DeferredResult;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.concurrent.ForkJoinPool;

@Slf4j
@CrossOrigin(origins="*")
@RestController
@RequestMapping("/async")
public class PushController  {
    @GetMapping("/async-deferredresult")
    public DeferredResult<ResponseEntity<?>> heandleReqDefResult(PushReq pushReq) {
        log.info("Received async-deferredResult reqeust");
        DeferredResult<ResponseEntity<?>> output = new DeferredResult<>();
//        DeferredResult<ResponseEntity<?>> deferredResult = new DeferredResult<>();
//
//        deferredResult.onTimeout(()->
//                deferredResult.setErrorResult(
//                        ResponseEntity.status(HttpStatus.REQUEST_TIMEOUT)
//                        .body("Request timeout occurred.")
//                ));

        ForkJoinPool.commonPool().submit(() -> {
            log.info("Processing in separate thread");
            try {
                log.info("으에엥1");
                Thread.sleep(3000);
                log.info("으에엥2");
            } catch (InterruptedException e) {

            }
//            deferredResult.setResult(ResponseEntity.ok("OK"));
            log.info("으에엥3");
            output.setResult(ResponseEntity.ok("OK"));
        });

        log.info("servlet thread freed" + output);
        //org.springframework.web.context.request.async.DeferredResult@3e4ccf48
        return output;
    }
}