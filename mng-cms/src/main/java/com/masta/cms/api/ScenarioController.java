package com.masta.cms.api;

import com.masta.cms.auth.jwt.JwtTokenProvider;
import com.masta.cms.model.PerformReq;
import com.masta.cms.model.SceneReq;
import com.masta.cms.service.ScenarioService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/scene")
public class ScenarioController {

    private final ScenarioService scenarioService;
    private final JwtTokenProvider jwtTokenProvider;

    public ScenarioController(ScenarioService scenarioService, JwtTokenProvider jwtTokenProvider) {
        this.scenarioService = scenarioService;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @GetMapping("/{uid}")
    public ResponseEntity getUserRecentScenario(@RequestHeader(value="Authentiation") String authentication,
                                                @PathVariable final int uid) {
        jwtTokenProvider.getUser(authentication, "ROLE_USER");
        return new ResponseEntity<>(scenarioService.getScenarioByType(uid), HttpStatus.OK);
    }

    @GetMapping("/{uid}/chc")
    public ResponseEntity getUserAnswer(@RequestHeader(value="Authentiation") String authentication,
                                        @PathVariable final int uid,
                                        @RequestParam("type") final int type,
                                        @RequestParam("ch") final int ch,
                                        @RequestParam("sc") final int sc) {
        jwtTokenProvider.getUser(authentication, "ROLE_USER");
        log.info("Param : " + type + ch + sc);
        SceneReq sceneReq = new SceneReq();
        sceneReq.setUid(uid);
        sceneReq.setType(type);
        sceneReq.setChapter(ch);
        sceneReq.setScene(sc);
        return new ResponseEntity<>(scenarioService.getScriptAnsWithSceneReq(sceneReq), HttpStatus.OK);
    }

    @PostMapping("/chc")
    public ResponseEntity postUserChoice(@RequestHeader(value="Authentiation") String authentication,
                                         @RequestBody SceneReq sceneReq) {
        jwtTokenProvider.getUser(authentication, "ROLE_USER");
        log.info("scnenReq in controller : " + sceneReq);
        return new ResponseEntity<>(scenarioService.postChoice(sceneReq), HttpStatus.OK);
    }

    @PostMapping("/start")
    public ResponseEntity startScenario(@RequestHeader(value="Authentiation") String authentication,
                                        @RequestBody PerformReq sceneReq) {
        jwtTokenProvider.getUser(authentication, "ROLE_USER");
        return new ResponseEntity<>(scenarioService.editPrgsScenario(sceneReq.getUid(), sceneReq.getType(), sceneReq.getChapter(), sceneReq.getScene(), 0), HttpStatus.OK);
    }

    @PutMapping("/end")
    public ResponseEntity endScenario(@RequestHeader(value="Authentiation") String authentication,
                                      @RequestBody PerformReq sceneReq) {
        jwtTokenProvider.getUser(authentication, "ROLE_USER");
        return new ResponseEntity<>(scenarioService.editPrgsScenario(sceneReq.getUid(), sceneReq.getType(), sceneReq.getChapter(), sceneReq.getScene(), 1), HttpStatus.OK);
    }



}
