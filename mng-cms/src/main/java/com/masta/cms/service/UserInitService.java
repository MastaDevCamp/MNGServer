package com.masta.cms.service;

import com.masta.cms.auth.dto.UserDto;
import com.masta.cms.dto.Favor;
import com.masta.cms.dto.UserDetail;
import com.masta.cms.mapper.PartnerMapper;
import com.masta.cms.mapper.UserInfoMapper;
import com.masta.cms.model.PartnerReq;
import com.masta.cms.model.UserReq;
import com.masta.core.response.DefaultRes;
import com.masta.core.response.ResponseMessage;
import com.masta.core.response.StatusCode;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.util.Date;

@Slf4j
@Service
public class UserInitService {
    private UserInfoMapper userInfoMapper;
    private PartnerMapper partnerMapper;

    public UserInitService(UserInfoMapper userInfoMapper, PartnerMapper partnerMapper) {
        this.userInfoMapper = userInfoMapper;
        this.partnerMapper = partnerMapper;
    }

    public DefaultRes createNewUser(final Long usernum) {
        try {
            log.info("서비스 내 사용자 넘 : " + usernum);

            Favor favor = partnerMapper.findDefaultFavor();
            UserDetail userReq = userInfoMapper.findUserDefaultValue();

            UserReq defaultUser = new UserReq();
            defaultUser.setPushonoff(userReq.getPushonoff());
            defaultUser.setGold(userReq.getGold());
            defaultUser.setRuby(userReq.getRuby());
            defaultUser.setHeart(userReq.getHeart());
            defaultUser.setReset(userReq.getReset());
            Date now = new Date();
            defaultUser.setCharge_at(now);

            log.info("DefaultUser Res : " + defaultUser);
            userInfoMapper.createNewUser(usernum, defaultUser);
            int userId = userInfoMapper.getUseridWithNum(usernum);
            log.info("사용자 아이디 : "+ userId);
            for(int i=1; i<5; i++)
            {
                log.info("i : "+i);
                PartnerReq defaultPartner = new PartnerReq();
                log.info("partner : "+defaultPartner);
                defaultPartner.setPartner(i);
                log.info("check : "+defaultPartner.getPartner());
                defaultPartner.setLike(favor.getLike());
                log.info("check : "+defaultPartner.getLike());
                defaultPartner.setTrust(favor.getTrust());
                log.info("check : "+defaultPartner.getTrust());

                log.info("DefaultPartner["+ i + "] Res : " + defaultPartner);

                partnerMapper.insertDefaultPartner(userId, defaultPartner);
            }
            return DefaultRes.res(StatusCode.CREATE, ResponseMessage.CREATE_NEW_USER);
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
