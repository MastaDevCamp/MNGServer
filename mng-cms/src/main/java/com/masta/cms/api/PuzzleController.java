package com.masta.cms.api;

import com.masta.cms.auth.jwt.JwtTokenProvider;
import com.masta.cms.model.PuzzleReq;
import com.masta.cms.service.PuzzleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/puzzle")
public class PuzzleController {
    private final PuzzleService puzzleService;
    private final JwtTokenProvider jwtTokenProvider;

    public PuzzleController(PuzzleService puzzleService, JwtTokenProvider jwtTokenProvider) {
        this.puzzleService = puzzleService;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @GetMapping("/{uid}")
    public ResponseEntity getPuzzleInfo(@RequestHeader(value="Authentiation") String authentication,
                                        @PathVariable final int uid) {
        jwtTokenProvider.getUser(authentication, "ROLE_USER");
        return new ResponseEntity<>(puzzleService.getPuzzleInfo(uid), HttpStatus.OK);
    }

    @PostMapping("/{uid}")
    public ResponseEntity postPuzzleInfo(@RequestHeader(value="Authentiation") String authentication,
                                         @PathVariable final int uid,
                                         @RequestBody final PuzzleReq puzzleReq) {
        jwtTokenProvider.getUser(authentication, "ROLE_USER");
        return new ResponseEntity<>(puzzleService.postPuzzleInfo(uid, puzzleReq.getPartner(), puzzleReq.getPuzzle(), puzzleReq.getPieces()), HttpStatus.OK);
    }

    @PutMapping("/{uid}")
    public ResponseEntity putPuzzleInfo(@RequestHeader(value="Authentiation") String authentication,
                                        @PathVariable final int uid,
                                        @RequestBody final PuzzleReq puzzleReq) {
        jwtTokenProvider.getUser(authentication, "ROLE_USER");
        return new ResponseEntity<>(puzzleService.putPuzzleInfo(uid, puzzleReq.getPartner(), puzzleReq.getPuzzle(), puzzleReq.getPieces()), HttpStatus.OK);
    }

}