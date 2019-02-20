package com.masta.cms.service;

import com.masta.cms.dto.Notice;
import com.masta.cms.mapper.NoticeMapper;
import com.masta.cms.model.NoticeReq;
import com.masta.core.response.DefaultRes;
import com.masta.core.response.ResponseMessage;
import com.masta.core.response.StatusCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.util.List;


@Slf4j
@Service
public class NoticeService {
    private final NoticeMapper noticeMapper;

    public NoticeService(final NoticeMapper noticeMapper){
        this.noticeMapper = noticeMapper;
    }


    //공지 등록
    public DefaultRes postNotice(final NoticeReq noticeReq){
        try {
            noticeMapper.insertNotice(noticeReq);
            return DefaultRes.res(StatusCode.CREATE, "Create Notice");
        }
        catch (Exception e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            log.error(e.getMessage());
            return DefaultRes.res(StatusCode.DB_ERROR, ResponseMessage.DB_ERROR);
        }
    }

    public DefaultRes getNotice() {
        try {
            List<Notice> notices = noticeMapper.findPerfectlyAllNotice();
            return DefaultRes.res(StatusCode.OK, "Get All Notices", notices);
        } catch (Exception e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            log.error(e.getMessage());
            return DefaultRes.res(StatusCode.DB_ERROR, ResponseMessage.DB_ERROR);
        }
    }

    //타입별 공지 보기
    public DefaultRes getNoticeByType(final int type) {
        try {
            List<Notice> notices = noticeMapper.findAllNotice(type);
            return DefaultRes.res(StatusCode.OK, "Get Notice By Type", notices);
        } catch (Exception e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            log.error(e.getMessage());
            return DefaultRes.res(StatusCode.DB_ERROR, ResponseMessage.DB_ERROR);
        }
    }

    public DefaultRes getOneNoticeById(final int notice_id) {
        try {
            Notice notice = noticeMapper.findOneNotice(notice_id);
            return DefaultRes.res(StatusCode.OK, "Get One Notice By Id", notice);
        } catch (Exception e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            log.error(e.getMessage());
            return DefaultRes.res(StatusCode.DB_ERROR, ResponseMessage.DB_ERROR);
        }
    }

    //공지 수정
    public DefaultRes updateNoticeById(final NoticeReq noticeReq, final int notice_id) {
        try {
            noticeMapper.updateNotice(noticeReq, notice_id);
            return DefaultRes.res(StatusCode.OK, "Update Notice");
        } catch (Exception e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            log.error(e.getMessage());
            return DefaultRes.res(StatusCode.DB_ERROR, ResponseMessage.DB_ERROR);
        }
    }

    public DefaultRes deleteNoticeById(final int notice_id) {
        try {
            noticeMapper.deleteNotice(notice_id);
            return DefaultRes.res(StatusCode.OK, "Delete Notice");
        } catch (Exception e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            log.error(e.getMessage());
            return DefaultRes.res(StatusCode.DB_ERROR, ResponseMessage.DB_ERROR);
        }
    }
}
