package com.masta.cms.service;

import com.masta.cms.mapper.HistoryMapper;
import com.masta.core.response.DefaultRes;
import com.masta.core.response.ResponseMessage;
import com.masta.core.response.StatusCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.util.List;

@Slf4j
@Service
public class HistoryService {
    private final HistoryMapper historyMapper;
    public HistoryService(HistoryMapper historyMapper) {
        this.historyMapper = historyMapper;
    }

    public DefaultRes getCGInfoWithUid(final int uid) {
        try {
            List<Integer> histories = historyMapper.findCGWithUid(uid);
            return DefaultRes.res(StatusCode.OK, ResponseMessage.FIND_HISTORY, histories);
        }
        catch (Exception e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            log.error(e.getMessage());
            return DefaultRes.res(StatusCode.DB_ERROR, ResponseMessage.DB_ERROR);
        }
    }

    public DefaultRes getEndingInfoWithUid(final int uid) {
        try {
            List<Integer> histories = historyMapper.findEndingWithUid(uid);
            return DefaultRes.res(StatusCode.OK, ResponseMessage.FIND_HISTORY, histories);
        }
        catch (Exception e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            log.error(e.getMessage());
            return DefaultRes.res(StatusCode.DB_ERROR, ResponseMessage.DB_ERROR);
        }
    }



    public DefaultRes postCGinHistory(final int uid, final int cg) {
        try {
            historyMapper.insertCGinHistory(uid, cg);
            return DefaultRes.res(StatusCode.OK, ResponseMessage.REGISTER_HISTORY);
        }
        catch (Exception e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            log.error(e.getMessage());
            return DefaultRes.res(StatusCode.DB_ERROR, ResponseMessage.DB_ERROR);
        }
    }

    public DefaultRes postEndinginHistory(final int uid, final int ending) {
        try {
            historyMapper.insertEndinginHistory(uid, ending);
            return DefaultRes.res(StatusCode.OK, ResponseMessage.REGISTER_HISTORY);
        }
        catch (Exception e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            log.error(e.getMessage());
            return DefaultRes.res(StatusCode.DB_ERROR, ResponseMessage.DB_ERROR);
        }
    }
}
