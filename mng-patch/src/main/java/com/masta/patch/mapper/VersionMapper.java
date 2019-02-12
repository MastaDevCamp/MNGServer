package com.masta.patch.mapper;

import com.masta.patch.dto.VersionLog;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface VersionMapper {

    @Insert("INSERT INTO version_log(version, patch, full) VALUES(#{log.version}, #{log.patch}, #{log.full})")
    void newVersionSave(@Param("log") final VersionLog log);

    @Select("SELECT * FROM version_log ORDER BY id DESC LIMIT 1")
    VersionLog latestVersion();

}
