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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;


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
            return DefaultRes.res(StatusCode.CREATE, ResponseMessage.REGISTER_NOTICE);
        }
        catch (Exception e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            log.error(e.getMessage());
            return DefaultRes.res(StatusCode.DB_ERROR, ResponseMessage.DB_ERROR);
        }
    }

    //공지사항 전부 가져오기
    public DefaultRes getNotice() {
        try {
            List<Notice> notices = noticeMapper.findPerfectlyAllNotice();
            return DefaultRes.res(StatusCode.OK, ResponseMessage.FIND_NOTICE, notices);
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
            return DefaultRes.res(StatusCode.OK, ResponseMessage.FIND_NOTICE, notices);
        } catch (Exception e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            log.error(e.getMessage());
            return DefaultRes.res(StatusCode.DB_ERROR, ResponseMessage.DB_ERROR);
        }
    }

    //공지 하나 가져오기
    public DefaultRes getOneNoticeById(final int notice_id) {
        try {
            Notice notice = noticeMapper.findOneNotice(notice_id);
            return DefaultRes.res(StatusCode.OK, ResponseMessage.FIND_NOTICE, notice);
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
            return DefaultRes.res(StatusCode.OK, ResponseMessage.UPDATE_NOTICE);
        } catch (Exception e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            log.error(e.getMessage());
            return DefaultRes.res(StatusCode.DB_ERROR, ResponseMessage.DB_ERROR);
        }
    }

    //공지 삭제
    public DefaultRes deleteNoticeById(final int notice_id) {
        try {
            noticeMapper.deleteNotice(notice_id);
            return DefaultRes.res(StatusCode.OK, ResponseMessage.DELETE_NOTICE);
        } catch (Exception e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            log.error(e.getMessage());
            return DefaultRes.res(StatusCode.DB_ERROR, ResponseMessage.DB_ERROR);
        }
    }

    //공지사항 유효기간 검사
    public DefaultRes checkNoticePeriod() {
        try {
            SimpleDateFormat mSimpleDateFormat = new SimpleDateFormat ( "yyyy-MM-dd HH:mm:ss", Locale.KOREA );
            Date nowDate = new Date();
            log.info("now time : " + mSimpleDateFormat.format(nowDate));
            String nowDateFormatted = mSimpleDateFormat.format(nowDate);
            List<Notice> notices = noticeMapper.findPerfectlyAllNotice();
            String tempDateStr;
            ArrayList<Integer> deleteIdx = new ArrayList<>();
            List<Notice> resultNotice = new ArrayList<Notice>();
            for(int i=0; i<notices.size(); i++) {
                tempDateStr = mSimpleDateFormat.format(notices.get(i).getFinish_at());
                int result = nowDateFormatted.compareTo(tempDateStr);
                if (result > 0) {
                    resultNotice.add(noticeMapper.findOneNotice(notices.get(i).getNotice_id()));
//                    noticeMapper.deleteNotice(i);

                }
            }
            return DefaultRes.res(StatusCode.OK, ResponseMessage.CHECK_INVALID_NOTICE, resultNotice);
//            return DefaultRes.res(StatusCode.OK, ResponseMessage.CHECK_INVALID_NOTICE);

        } catch (Exception e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            log.error(e.getMessage());
            return DefaultRes.res(StatusCode.DB_ERROR, ResponseMessage.DB_ERROR);
        }
    }
}
