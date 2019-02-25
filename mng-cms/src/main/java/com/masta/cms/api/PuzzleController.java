package com.masta.cms.api;

import com.masta.cms.auth.dto.UserDto;
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

    @GetMapping("/")
    public ResponseEntity getPuzzleInfo(@RequestHeader(value="Authentiation") String authentication) {
        UserDto userDto = jwtTokenProvider.getUser(authentication, "ROLE_USER");
        Long usernum = userDto.getUsernum();

        return new ResponseEntity<>(puzzleService.getPuzzleInfo(usernum), HttpStatus.OK);
    }

    @PostMapping("/")
    public ResponseEntity postPuzzleInfo(@RequestHeader(value="Authentiation") String authentication,
                                         @RequestBody final PuzzleReq puzzleReq) {
        UserDto userDto = jwtTokenProvider.getUser(authentication, "ROLE_USER");
        Long usernum = userDto.getUsernum();
        return new ResponseEntity<>(puzzleService.postPuzzleInfo(usernum, puzzleReq.getPartner(), puzzleReq.getPuzzle(), puzzleReq.getPieces()), HttpStatus.OK);
    }

    @PutMapping("/")
    public ResponseEntity putPuzzleInfo(@RequestHeader(value="Authentiation") String authentication,
                                        @RequestBody final PuzzleReq puzzleReq) {
        UserDto userDto = jwtTokenProvider.getUser(authentication, "ROLE_USER");
        Long usernum = userDto.getUsernum();
        return new ResponseEntity<>(puzzleService.putPuzzleInfo(usernum, puzzleReq.getPartner(), puzzleReq.getPuzzle(), puzzleReq.getPieces()), HttpStatus.OK);
    }

}