package com.masta.cms.mapper;

import com.masta.cms.dto.Mailbox;
import com.masta.cms.dto.UserRewarded;
import com.masta.cms.model.MailboxReq;
import org.apache.ibatis.annotations.*;

import java.util.Date;
import java.util.List;

@Mapper
public interface MailboxMapper {
    //해당 유저가 우편함을 이미 받았는지 확인
    @Select("SELECT * FROM rewarded WHERE uid = #{uid} AND mail_id = #{mail_id}")
    UserRewarded getUserMailboxAction(@Param("uid") final int uid, @Param("mail_id") final int mail_id);

    //해당 유저가 열람하지 않은 메일박스 목록 호출
    @Select("SELECT * FROM mailbox WHERE mail_id NOT IN (SELECT mail_id FROM rewarded WHERE uid=#{uid})")
    List<Mailbox> getNotOpenedMailList(@Param("uid") final int uid);

    //메일박스를 받은 적이 없으면 ISNERT
    @Insert("INSERT INTO rewarded (uid, mail_id) VALUES (#{uid}, #{mail_id})")
    void insertUserInAction(@Param("uid") final int uid, @Param("mail_id") final int mail_id);

    //새 메일박스 등록
    @Insert("INSERT INTO mailbox (title, contents, finish_at) VALUES (#{title}, #{contents}, #{finish_at})")
    void insertMail(@Param("title") final String title, @Param("contents") final String contents, @Param("finish_at") final Date finish_at);

    //메일 수정
    @Update("UPDATE mailbox SET title=#{mailbox.title}, contents=#{mailbox.contents}, finish_at=#{mailbox.finish_at} WHERE mail_id=#{mail_id}")
    void modifyMail(@Param("mailbox") final MailboxReq mailbox, @Param("mail_id") final int mail_id);

    @Delete("DELETE FROM mailbox WHERE mail_id=#{mail_id}")
    void deleteMail(@Param("mail_id") final int mail_id);

    //메일박스 전체 조회
    @Select("SELECT * FROM mailbox")
    List<Mailbox> getAllMailbox();

    //메일박스 하나 조회
    @Select("SELECT * FROM mailbox WHERE mail_id = #{mail_id}")
    Mailbox getOneMailbox(@Param("mail_id") final int mail_id);
}
