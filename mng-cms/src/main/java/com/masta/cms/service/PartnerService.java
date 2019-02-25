package com.masta.cms.service;


import com.masta.cms.dto.Favor;
import com.masta.cms.mapper.PartnerMapper;
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
public class PartnerService {
    private final PartnerMapper partnerMapper;
    private final UserInfoMapper userInfoMapper;
    public PartnerService(PartnerMapper partnerMapper, UserInfoMapper userInfoMapper) {
        this.partnerMapper = partnerMapper;
        this.userInfoMapper = userInfoMapper;
    }



    public DefaultRes getFavor(final Long usernum) {
        try {
            List<Favor> favors = partnerMapper.findAllFavor(userInfoMapper.getUseridWithNum(usernum));
            return DefaultRes.res(StatusCode.OK, ResponseMessage.FIND_FAVOR, favors);
        }
        catch (Exception e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            log.error(e.getMessage());
            return DefaultRes.res(StatusCode.DB_ERROR, ResponseMessage.DB_ERROR);
        }
    }

    public DefaultRes getFavor() {
        try {
            List<Favor> favors = partnerMapper.findAllFavor(0);
            return DefaultRes.res(StatusCode.OK, ResponseMessage.FIND_FAVOR, favors);
        }
        catch (Exception e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            log.error(e.getMessage());
            return DefaultRes.res(StatusCode.DB_ERROR, ResponseMessage.DB_ERROR);
        }
    }

    public DefaultRes editFavor(final Long usernum, final int partner, final int like, final int trust) {
        try {
            int uid = userInfoMapper.getUseridWithNum(usernum);
            Favor favor = partnerMapper.findOneFavor(uid, partner);
            int newLike = favor.getLike() + like;
            int newTrust = favor.getTrust() + trust;
            partnerMapper.updateFavor(newLike, newTrust, uid, partner);
            return DefaultRes.res(StatusCode.OK, ResponseMessage.MODIFY_FAVOR);
        }
        catch (Exception e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            log.error(e.getMessage());
            return DefaultRes.res(StatusCode.DB_ERROR, ResponseMessage.DB_ERROR);
        }
    }

    public DefaultRes editFavorDefualt(final int uid, final int partner, final int like, final int trust) {
        try {
            Favor favor = partnerMapper.findOneFavor(uid, partner);
            int newLike = favor.getLike() + like;
            int newTrust = favor.getTrust() + trust;
            partnerMapper.updateFavor(newLike, newTrust, uid, partner);
            return DefaultRes.res(StatusCode.OK, ResponseMessage.MODIFY_FAVOR);
        }
        catch (Exception e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            log.error(e.getMessage());
            return DefaultRes.res(StatusCode.DB_ERROR, ResponseMessage.DB_ERROR);
        }
    }


}
