package com.masta.cms.service;

import com.masta.cms.mapper.PartnerMapper;
import com.masta.cms.mapper.UserInfoMapper;
import com.masta.core.response.DefaultRes;
import com.masta.core.response.ResponseMessage;
import com.masta.core.response.StatusCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

@Slf4j
@Service
public class UserInitService {
    private UserInfoMapper userInfoMapper;
    private PartnerMapper partnerMapper;

    public UserInitService(UserInfoMapper userInfoMapper, PartnerMapper partnerMapper) {
        this.userInfoMapper = userInfoMapper;
        this.partnerMapper = partnerMapper;
    }

    public DefaultRes createNewUser(final int uid) {
        try {
            userInfoMapper.createNewUser(uid);
            return DefaultRes.res(StatusCode.CREATE, "Created New User");
        } catch (Exception e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            log.error(e.getMessage());
            return DefaultRes.res(StatusCode.DB_ERROR, ResponseMessage.DB_ERROR);
        }
    }

//    public DefaultRes initUser(final UserReq userReq, final int uid) {
//        try {
//            userInfoMapper.initUser(userReq, uid);
//            return DefaultRes.res(StatusCode.CREATED, "")
//        }
//    }
}
