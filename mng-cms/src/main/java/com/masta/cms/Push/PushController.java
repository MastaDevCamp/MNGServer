package com.masta.cms.Push;

import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Slf4j
@RestController
@RequestMapping("/push")
public class PushController {
    private PushService pushService;
    public PushController(PushService pushService) {
        this.pushService = pushService;
    }

    @PostMapping("")
    public ResponseEntity send(@RequestBody Map<String, Object> paramInfo) throws JSONException
    {
        Map<String, Object> retVal = new HashMap<String, Object>();

        //FCM 메시지 전송
        JSONObject body = new JSONObject();

        //DB에 지정된 여러 개의 토큰을 가져와 설정
        List<String> tokenList = new ArrayList<String>();
        //DB과정
        //tokenList.add("");

        JSONArray array = new JSONArray();

        for (int i=0; i<tokenList.size(); i++) {
            array.put(tokenList.get(i));
        }

        //여러 메시지일 경우 registration_id
        //단일 메시지일 경우 to를 사용한다.
        body.put("registration_id : ", array);

        JSONObject notification = new JSONObject();
        notification.put("title", "FCM Test APP");
        notification.put("body", paramInfo.get("message"));

        body.put("notification", notification);

        log.info("body : " + body.toString());

        HttpEntity<String> request = new HttpEntity<>(body.toString());

        CompletableFuture<String> pushNotification = pushService.send(request);
        CompletableFuture.allOf(pushNotification).join();

        try {
            String firebaseResponse = pushNotification.get();
            return new ResponseEntity<>(firebaseResponse, HttpStatus.OK);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        return new ResponseEntity<>("Push Notification Error!", HttpStatus.BAD_REQUEST);
    }
}
