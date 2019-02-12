package com.masta.cms.service;

import com.masta.cms.dto.UserDetail;
import com.masta.cms.mapper.UserInfoMapper;
import com.masta.core.response.DefaultRes;
import com.masta.core.response.ResponseMessage;
import com.masta.core.response.StatusCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

@Slf4j
@Service
public class UserDetailService {
    private final UserInfoMapper userInfoMapper;
    public UserDetailService(final UserInfoMapper userInfoMapper) {
        this.userInfoMapper = userInfoMapper;
    }

    //디테일 정보 조회
    public DefaultRes getUserDetailWithId(final int uid){
        try {
            final UserDetail userDetail = userInfoMapper.findUserDetail(uid);
            if (userDetail == null) {
                return DefaultRes.res(StatusCode.NOT_FOUND, ResponseMessage.NOT_FOUND_USER);
            } else {
                return DefaultRes.res(StatusCode.OK, ResponseMessage.READ_USER, userDetail);
            }
        } catch (Exception e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            log.error(e.getMessage());
            return DefaultRes.res(StatusCode.DB_ERROR, ResponseMessage.DB_ERROR);
        }
    }

    //닉네임 변경
    public DefaultRes updateUserNickname(final String nickname, final int uid) {
        try {
            userInfoMapper.updateUserNickname(nickname, uid);
            return DefaultRes.res(StatusCode.OK, "Update User Nickname");
        } catch (Exception e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            log.error(e.getMessage());
            return DefaultRes.res(StatusCode.DB_ERROR, ResponseMessage.DB_ERROR);
        }
    }

    //푸시 온오프 변경
    public DefaultRes updateUserPushOnoff(final int uid){
        try {
            final UserDetail userDetail = userInfoMapper.findUserDetail(uid);
            int userPush = userDetail.getPushonoff();
            if (userPush == 0)
                userInfoMapper.updateUserOnoff(1, uid);
            else if (userPush == 1)
                userInfoMapper.updateUserOnoff(0, uid);
            return DefaultRes.res(StatusCode.OK, "Modify Push OnOff");
        } catch (Exception e){
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            log.error(e.getMessage());
            return DefaultRes.res(StatusCode.DB_ERROR, ResponseMessage.DB_ERROR);
        }
    }

//    public DefaultRes onPush(final int onoff, final int uid) {
//        try {
//            userInfoMapper.updateUserOnoff(onoff, uid);
//            return DefaultRes.res(StatusCode.OK, "Modify Push On");
//        } catch (Exception e){
//            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
//            log.error(e.getMessage());
//            return DefaultRes.res(StatusCode.DB_ERROR, ResponseMessage.DB_ERROR);
//        }
//    }
//
//    public DefaultRes offPush(final int onoff, final int uid) {
//        try {
//            userInfo
//        }
//    }
    //유저 재화 정보 변경
    public DefaultRes updateUserMoney(final int ruby, final int gold, final int uid){
        try {
            //원래 있던 재화 체크
            final UserDetail userDetail = userInfoMapper.findUserDetail(uid);
            if (userDetail == null) {
                return DefaultRes.res(StatusCode.NOT_FOUND, ResponseMessage.NOT_FOUND_USER);
            } else {
                userInfoMapper.updateUserMoneyInfo(userDetail.getRuby()+ruby, userDetail.getGold() + gold, uid);
                return DefaultRes.res(StatusCode.OK, "재화 정보 변경");
            }
        }
        catch (Exception e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            log.error(e.getMessage());
            return DefaultRes.res(StatusCode.DB_ERROR, ResponseMessage.DB_ERROR);
        }
    }

    //유저 하트 1개 소모
    public DefaultRes spendOneHeart(final int uid) {
        try {
            final UserDetail userDetail = userInfoMapper.findUserDetail(uid);
            if (userDetail == null) {
                return DefaultRes.res(StatusCode.NOT_FOUND, ResponseMessage.NOT_FOUND_USER);
            } else {
                int newHeart = userDetail.getHeart() - 1;
                userInfoMapper.updateUserHeart(newHeart, uid);
                return DefaultRes.res(StatusCode.OK, "Update Heart to Minus One");
            }
        } catch (Exception e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            log.error(e.getMessage());
            return DefaultRes.res(StatusCode.DB_ERROR, ResponseMessage.DB_ERROR);
        }
    }

    //유저 하트 1개 충전
//    public DefaultRes chargeOneHeart(final int uid) {
//        try {
//            final UserDetail userDetail = userInfoMapper.findUserDetail(uid);
//            if (userDetail == null) {
//                return DefaultRes.res(StatusCode.NOT_FOUND, ResponseMessage.NOT_FOUND_USER);
//            } else {
//                int heart = userDetail.getHeart() + 1;
//                d
//                userInfoMapper.updateUserHeart(heart, uid);
//                userInfoMapper.
//            }
//        }
//    }

    //유저 하트 변경
    public DefaultRes updateHeart(final int heart, final int uid) {
        try {
            final UserDetail userDetail = userInfoMapper.findUserDetail(uid);
            if (userDetail == null) {
                return DefaultRes.res(StatusCode.NOT_FOUND, ResponseMessage.NOT_FOUND_USER);
            } else {
                int newHeart = userDetail.getHeart() + heart;
                userInfoMapper.updateUserHeart(newHeart, uid);
                return DefaultRes.res(StatusCode.OK, "Update Heart Count");
            }
        } catch (Exception e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            log.error(e.getMessage());
            return DefaultRes.res(StatusCode.DB_ERROR, ResponseMessage.DB_ERROR);
        }
    }


}
