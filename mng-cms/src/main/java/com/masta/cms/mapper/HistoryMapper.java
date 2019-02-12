package com.masta.cms.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface HistoryMapper {
    @Select("SELECT cg FROM cg WHERE uid=#{uid}")
    List<Integer> findCGWithUid(@Param("uid") final int uid);

    @Select("SELECT ending FROM ending WHERE uid=#{uid}")
    List<Integer> findEndingWithUid(@Param("uid") final int uid);

    @Insert("INSERT INTO cg (uid, cg) VALUES (#{uid}, #{cg})")
    void insertCGinHistory(@Param("uid") final int uid, @Param("cg") final int cg);

    @Insert("INSERT INTO ending (uid, ending) VALUES (#{uid}, #{ending})")
    void insertEndinginHistory(@Param("uid") final int uid, @Param("ending") final int ending);
}
