package com.masta.cms.service;

import com.masta.cms.mapper.HistoryMapper;
import com.masta.cms.mapper.UserInfoMapper;
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
    private final UserInfoMapper userInfoMapper;

    public HistoryService(HistoryMapper historyMapper, UserInfoMapper userInfoMapper) {
        this.historyMapper = historyMapper;
        this.userInfoMapper = userInfoMapper;
    }

    public DefaultRes getCGInfoWithNum(final Long usernum) {
        try {
            int uid = userInfoMapper.getUseridWithNum(usernum);
            log.info("***************************uid**************************" + uid);
            List<Integer> histories = historyMapper.findCGWithUid(uid);
            return DefaultRes.res(StatusCode.OK, ResponseMessage.FIND_HISTORY, histories);
        }
        catch (Exception e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            log.error(e.getMessage());
            return DefaultRes.res(StatusCode.DB_ERROR, ResponseMessage.DB_ERROR);
        }
    }

    public DefaultRes getEndingInfoWithNum(final Long usernum) {
        try {
            int uid = userInfoMapper.getUseridWithNum(usernum);
            List<Integer> histories = historyMapper.findEndingWithUid(uid);
            return DefaultRes.res(StatusCode.OK, ResponseMessage.FIND_HISTORY, histories);
        }
        catch (Exception e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            log.error(e.getMessage());
            return DefaultRes.res(StatusCode.DB_ERROR, ResponseMessage.DB_ERROR);
        }
    }



    public DefaultRes postCGinHistory(final Long usernum, final int cg) {
        try {
            int uid = userInfoMapper.getUseridWithNum(usernum);
            historyMapper.insertCGinHistory(uid, cg);
            return DefaultRes.res(StatusCode.OK, ResponseMessage.REGISTER_HISTORY);
        }
        catch (Exception e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            log.error(e.getMessage());
            return DefaultRes.res(StatusCode.DB_ERROR, ResponseMessage.DB_ERROR);
        }
    }

    public DefaultRes postEndinginHistory(final Long usernum, final int ending) {
        try {
            int uid = userInfoMapper.getUseridWithNum(usernum);
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
