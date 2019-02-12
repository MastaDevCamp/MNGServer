package com.masta.cms.mapper;

import com.masta.cms.dto.Puzzle;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface PuzzleMapper {

    @Select("SELECT * FROM puzzle WHERE partner_id IN (SELECT partner_id FROM partner WHERE uid = #{uid})")
    List<Puzzle> getPuzzleInfoWithUid(@Param("uid") final int uid);

    @Select("SELECT * FROM puzzle WHERE partner_id IN (SELECT partner_id FROM partner WHERE uid = #{uid} AND partner=#{partner})")
    List<Puzzle> getPuzzleInfoWithPartner(@Param("uid") final int uid, @Param("partner") final int partner);

    @Insert("INSERT INTO puzzle (partner_id, puzzle, pieces) VALUES (#{partner_id}, #{puzzle}, #{pieces})")
    void insertPuzzleInfoWithPartner(@Param("partner_id") final int partner_id, @Param("puzzle") final int puzzle, @Param("pieces") final int pieces);

    @Update("UPDATE puzzle SET puzzle=#{puzzle}, pieces=#{pieces} WHERE partner_id=#{partner_id}")
    void updatePuzzleInfo(@Param("partner_id") final int partner_id, @Param("puzzle") final int puzzle, @Param("pieces") final int pieces);

    @Delete("DELETE FROM puzzle WHERE puzzle_id=#{puzzle_id}")
    void deletePuzzleWithIdx(@Param("puzzle_id") final int puzzle_id);

    @Delete("DELETE FROM puzzle WHERE partner_id=#{partner_id}")
    void deletePuzzleWithPartner(@Param("partner_id") final int partner_id);
}
