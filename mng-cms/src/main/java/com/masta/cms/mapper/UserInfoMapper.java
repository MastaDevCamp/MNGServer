package com.masta.cms.mapper;

import com.masta.cms.dto.UserDetail;
import com.masta.cms.model.UserReq;
import org.apache.ibatis.annotations.*;

@Mapper
public interface UserInfoMapper {
    //회원 디테일 전체 정보 조회
    @Select("SELECT * FROM userinfo WHERE uid = #{uid}")
    UserDetail findUserDetail(@Param("uid") final int uid);

    //회원 생성
    @Insert("INSERT INTO userinfo (uid) VALUES (#{uid})")
    void createNewUser(@Param("uid") final int uid);

    //회원 초기화
    @Update("UPDATE userinfo SET gold=#{user.gold}, ruby=#{user.ruby}, pushonoff=#{user.pushonoff}, heart=#{user.heart}, charge_at=#{user.charge_at} WHERE uid=#{uid}")
    void initUser(@Param("user") final UserReq user, @Param("uid") final int uid);

    //닉네임 변경
    @Update("UPDATE userinfo SET nickname = #{nickname} WHERE uid = #{uid}")
    void updateUserNickname(@Param("nickname") final String nickname, @Param("uid") final int uid);

    //푸시알람 온오프 정보 변경
    @Update("UPDATE userinfo SET pushonoff = #{onoff} WHERE uid = #{uid}")
    void updateUserOnoff(@Param("onoff") final int onoff, @Param("uid") final int uid);

    //회원 재화 정보 갱신
    @Update("UPDATE userinfo SET ruby = #{ruby}, gold = #{gold} WHERE uid = #{uid}")
    void updateUserMoneyInfo(@Param("ruby") final int ruby, @Param("gold") final int gold, @Param("uid") final int uid);

    //회원 하트 변경
    @Update("UPDATE userinfo SET heart = #{heart} WHERE uid = #{uid}")
    void updateUserHeart(@Param("heart") final int heart, @Param("uid") final int uid);
}
