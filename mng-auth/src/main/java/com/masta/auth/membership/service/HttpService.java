package com.masta.auth.membership.service;

import com.masta.auth.membership.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

@Slf4j
public class HttpService {
    JSONObject req = new JSONObject();
    public ResponseEntity InitCMS(String url, Long num) throws JSONException {

        RestTemplate restTemplate = new RestTemplate();

        req.put("usernum", num);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> entity = new HttpEntity<>(req.toString(),headers);
        ResponseEntity<String> exchange = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);
        log.info(exchange.toString());
        return new ResponseEntity(HttpStatus.OK );
    }
}
