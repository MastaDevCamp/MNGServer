package com.masta.cms.mapper;

import com.masta.cms.dto.Favor;
import com.masta.cms.model.PartnerReq;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface PartnerMapper {

    @Select("SELECT * FROM partner WHERE uid=0")
    Favor findDefaultFavor();

    @Select("SELECT * FROM partner WHERE uid = #{uid}")
    List<Favor> findAllFavor(@Param("uid") final int uid);

    @Select("SELECT * FROM partner WHERE uid = #{uid} AND partner = #{partner}")
    Favor findOneFavor(@Param("uid") final int uid, @Param("partner") final int partner);

    @Select("SELECT partner_id FROM partner WHERE uid=#{uid} AND partner=#{partner}")
    int findPartnerFavor(@Param("uid") final int uid, @Param("partner") final int partner);

    /*
    UPDATE 테이블이름

    SET 필드이름1=데이터값1, 필드이름2=데이터값2, ...

    WHERE 필드이름=데이터값
     */
    @Update("UPDATE partner SET partner.like=#{like}, partner.trust=#{trust} WHERE uid=#{uid} AND partner=#{partner}")
    void updateFavor(@Param("like") final int like, @Param("trust") final int trust, @Param("uid") final int uid, @Param("partner") final int partner);


    //파트너 초기화 설정
    @Insert("INSERT INTO partner (uid, partner, partner.like, trust) VALUES (#{uid}, #{partner.partner}, #{partner.like}, #{partner.trust})")
    void insertDefaultPartner(@Param("uid") final int uid, @Param("partner") final PartnerReq partner);

//    @Insert("INSERT INTO partner (uid, partner, like, trust) VALUES (#{uid}, #{partner}, #{like}, #{trust})")
//    void insertDefaultFavor(@Param("uid") final int uid, @Param("partner") final int partner, @Param("like") final int like, @Param("trust") final int trust);
}
