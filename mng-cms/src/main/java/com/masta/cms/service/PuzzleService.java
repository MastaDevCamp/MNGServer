package com.masta.cms.service;

import com.masta.cms.dto.Puzzle;
import com.masta.cms.mapper.PartnerMapper;
import com.masta.cms.mapper.PuzzleMapper;
import com.masta.core.response.DefaultRes;
import com.masta.core.response.ResponseMessage;
import com.masta.core.response.StatusCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.util.List;

@Slf4j
@Service
public class PuzzleService {
    private final PuzzleMapper puzzleMapper;
    private final PartnerMapper partnerMapper;
    public PuzzleService(PuzzleMapper puzzleMapper, PartnerMapper partnerMapper) {
        this.puzzleMapper = puzzleMapper;
        this.partnerMapper = partnerMapper;
    }

    public DefaultRes getPuzzleInfo(final int uid) {
        try {
            List<Puzzle> puzzleList = puzzleMapper.getPuzzleInfoWithUid(uid);
            if(puzzleList == null) {
                return DefaultRes.res(StatusCode.NOT_FOUND, "Not Exist User Puzzle");
            }
            return DefaultRes.res(StatusCode.OK, "Find User Puzzle", puzzleList);
        }
        catch (Exception e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            log.error(e.getMessage());
            return DefaultRes.res(StatusCode.DB_ERROR, ResponseMessage.DB_ERROR);
        }
    }

    public DefaultRes getPuzzleInfoWithPartner(final int uid, final int partner) {
        try {
            List<Puzzle> puzzleList = puzzleMapper.getPuzzleInfoWithPartner(uid, partner);
            if(puzzleList == null) {
                return DefaultRes.res(StatusCode.NOT_FOUND, "Not Exist User Puzzle");
            }
            return DefaultRes.res(StatusCode.OK, "Find User Puzzle", puzzleList);
        }
        catch (Exception e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            log.error(e.getMessage());
            return DefaultRes.res(StatusCode.DB_ERROR, ResponseMessage.DB_ERROR);
        }
    }

    public DefaultRes postPuzzleInfo(final int uid, final int partner, final int puzzle, final int pieces) {
        try {
            int partner_id = partnerMapper.findPartnerFavor(uid, partner);
            puzzleMapper.insertPuzzleInfoWithPartner(partner_id, puzzle, pieces);
            return DefaultRes.res(StatusCode.OK, "Register User Puzzle");
        }
        catch (Exception e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            log.error(e.getMessage());
            return DefaultRes.res(StatusCode.DB_ERROR, ResponseMessage.DB_ERROR);
        }
    }

    public DefaultRes putPuzzleInfo(final int uid, final int partner, final int puzzle, final int pieces) {
        try {
            int partner_id = partnerMapper.findPartnerFavor(uid, partner);
            log.info("partner_id : " + partner_id);
            puzzleMapper.updatePuzzleInfo(partner_id, puzzle, pieces);
            return DefaultRes.res(StatusCode.OK, "Update User Puzzle");
        }
        catch (Exception e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            log.error(e.getMessage());
            return DefaultRes.res(StatusCode.DB_ERROR, ResponseMessage.DB_ERROR);
        }
    }

    public DefaultRes deletePuzzleInfo(final int puzzle_id) {
        try {
            puzzleMapper.deletePuzzleWithIdx(puzzle_id);
            return DefaultRes.res(StatusCode.OK, "Delete User Puzzle with Puzzle");
        }
        catch (Exception e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            log.error(e.getMessage());
            return DefaultRes.res(StatusCode.DB_ERROR, ResponseMessage.DB_ERROR);
        }
    }

    public DefaultRes deletePuzzleInfoByPartner(final int partner_id) {
        try {
            puzzleMapper.deletePuzzleWithPartner(partner_id);
            return DefaultRes.res(StatusCode.OK, "Delete User Puzzle with Partner");
        }
        catch (Exception e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            log.error(e.getMessage());
            return DefaultRes.res(StatusCode.DB_ERROR, ResponseMessage.DB_ERROR);
        }
    }
}
