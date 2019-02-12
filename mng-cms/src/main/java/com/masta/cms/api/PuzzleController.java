package com.masta.cms.api;

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
    public PuzzleController(PuzzleService puzzleService) {
        this.puzzleService = puzzleService;
    }

    @GetMapping("/{uid}")
    public ResponseEntity getPuzzleInfo(@PathVariable final int uid) {
        return new ResponseEntity<>(puzzleService.getPuzzleInfo(uid), HttpStatus.OK);
    }

    @PostMapping("/{uid}")
    public ResponseEntity postPuzzleInfo(@PathVariable final int uid,
                                         @RequestBody final PuzzleReq puzzleReq) {
        return new ResponseEntity<>(puzzleService.postPuzzleInfo(uid, puzzleReq.getPartner(), puzzleReq.getPuzzle(), puzzleReq.getPieces()), HttpStatus.OK);
    }

    @PutMapping("/{uid}")
    public ResponseEntity putPuzzleInfo(@PathVariable final int uid,
                                        @RequestBody final PuzzleReq puzzleReq) {
        return new ResponseEntity<>(puzzleService.putPuzzleInfo(uid, puzzleReq.getPartner(), puzzleReq.getPuzzle(), puzzleReq.getPieces()), HttpStatus.OK);
    }

}