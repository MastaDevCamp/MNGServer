package com.masta.cms.mapper;

import com.masta.cms.dto.Notice;
import com.masta.cms.model.NoticeReq;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface NoticeMapper {
    @Select("SELECT * FROM notice")
    List<Notice> findPerfectlyAllNotice();

    //공지사항 타입별 조회
    @Select("SELECT * FROM notice WHERE type = #{type}")
    List<Notice> findAllNotice(@Param("type") int type);

    //공지사항 단일 조회
    @Select("SELECT * FROM notice WHERE notice_id = #{notice_id}")
    Notice findOneNotice(@Param("notice_id") int notice_id);

    //공지사항 등록
    @Insert("INSERT INTO notice (type, title, contents, finish_at, begin_at) VALUES (#{notice.type}, #{notice.title}, #{notice.contents}, #{notice.finish_at}, #{notice.begin_at})")
    void insertNotice(@Param("notice") NoticeReq noticeReq);

    //공지사항 수정
    @Update("UPDATE notice SET title=#{notice.title}, contents=#{notice.contents}, finish_at=#{notice.finish_at}, begin_at=#{notice.begin_at} WHERE notice_id=#{notice_id}")
    void updateNotice(@Param("notice") NoticeReq noticeReq, @Param("notice_id") int notice_id);

    @Delete("DELETE FROM notice WHERE notice_id=#{notice_id}")
    void deleteNotice(@Param("notice_id") int notice_id);

//    //공지사항 등록
//    @Insert("INSERT INTO notice (notice_title, notice_desc, notice_finish_time, notice_type) VALUES (#{notice.title}, #{notice.desc}, #{notice.finish_time}, #{notice.type})")
//    void insertNoticeInfo(@Param("notice") NoticeReq noticeReq);
//
//    //공지사항 타입별 조회
//    @Select("SELECT * FROM notice WHERE notice_type = #{type}")
//    List<NoticeInfo> findNoticeWithType(@Param("type") int type);
}
