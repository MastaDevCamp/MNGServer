package com.masta.cms.service;

import com.masta.cms.dto.Mailbox;
import com.masta.cms.dto.UserRewarded;
import com.masta.cms.mapper.MailboxMapper;
import com.masta.cms.model.MailboxReq;
import com.masta.core.response.DefaultRes;
import com.masta.core.response.ResponseMessage;
import com.masta.core.response.StatusCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

@Slf4j
@Service
public class MailboxService {
    private final MailboxMapper mailboxMapper;

    public MailboxService(final MailboxMapper mailboxMapper){
        this.mailboxMapper = mailboxMapper;
    }

    //우편함 전체 목록 보여주기
    public DefaultRes getAllMailboxWithUid(final int uid) {
        try {
            List<Mailbox> mailboxListNotOpened = mailboxMapper.getNotOpenedMailList(uid);
            return DefaultRes.res(StatusCode.OK, ResponseMessage.FIND_MAILBOX, mailboxListNotOpened);
        } catch (Exception e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            log.error(e.getMessage());
            return DefaultRes.res(StatusCode.DB_ERROR, ResponseMessage.DB_ERROR);
        }
    }


    //우편함 하나 보여주기
    public DefaultRes getOneMailbox(final int mail_id) {
        try {
            Mailbox oneMailboxInfoList = mailboxMapper.getOneMailbox(mail_id);
            return DefaultRes.res(StatusCode.OK, ResponseMessage.FIND_MAILBOX, oneMailboxInfoList);
        } catch (Exception e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            log.error(e.getMessage());
            return DefaultRes.res(StatusCode.DB_ERROR, ResponseMessage.DB_ERROR);
        }
    }

    //우편함 보상을 수령
    public DefaultRes postRewardedUser(final UserRewarded userRewarded) {
        try {
            int uid = userRewarded.getUid();
            int mail_id = userRewarded.getMail_id();
            log.info("uid : " + uid + " mail_id : " + mail_id);
            UserRewarded checkUserRewarded = mailboxMapper.getUserMailboxAction(uid, mail_id);
            log.info("???");
            if (checkUserRewarded == null) {
                log.info("Im in if");
                //이벤트 보상 수령
//                final UserMailboxReq mailboxReq = new UserMailboxReq();
//                mailboxReq.setUser_id(user_id);
//                mailboxReq.setMailbox_id(mailbox_id);
                log.info("uid : ", uid);
                log.info("mail_id : ", mail_id);
                mailboxMapper.insertUserInAction(uid, mail_id);
                return DefaultRes.res(StatusCode.OK, ResponseMessage.GET_REWARD_MAILBOX);
            }
            return DefaultRes.res(StatusCode.NO_CONTENT, ResponseMessage.ALREADY_REWARD);

        } catch (Exception e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            log.error(e.getMessage());
            return DefaultRes.res(StatusCode.DB_ERROR, ResponseMessage.DB_ERROR);
        }
    }

    //이벤트 등록 - 이하 안씀
    public DefaultRes registerMail(final MailboxReq mailboxReq) {
        try {
            log.info("MailboxService");

            String title = mailboxReq.getTitle();
            String contents = mailboxReq.getContents();
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.KOREA);
            Date finish_at = simpleDateFormat.parse(mailboxReq.getFinish_at());

            log.info("finishdate parsing");

            log.info("im in front of mapper");
            mailboxMapper.insertMail(title, contents, finish_at);
            return DefaultRes.res(StatusCode.CREATE, "Create Event Mail");
        } catch (Exception e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            log.error(e.getMessage());
            return DefaultRes.res(StatusCode.DB_ERROR, ResponseMessage.DB_ERROR);
        }
    }

    public DefaultRes modifyMail(final MailboxReq mailbox, final int mail_id) {
        try {
            mailboxMapper.modifyMail(mailbox, mail_id);
            return DefaultRes.res(StatusCode.OK, "Modify Event Mail");
        } catch (Exception e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            log.error(e.getMessage());
            return DefaultRes.res(StatusCode.DB_ERROR, ResponseMessage.DB_ERROR);
        }
    }

    public DefaultRes deleteMail(final int mail_id) {
        try {
            mailboxMapper.deleteMail(mail_id);
            return DefaultRes.res(StatusCode.OK, "Delete Event Mail");
        } catch (Exception e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            log.error(e.getMessage());
            return DefaultRes.res(StatusCode.DB_ERROR, ResponseMessage.DB_ERROR);
        }
    }
}
