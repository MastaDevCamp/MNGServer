package com.masta.patch.mapper;

import com.masta.patch.dto.VersionLog;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface VersionMapper {

    @Insert("INSERT INTO version_log(version, patch, full) VALUES(#{log.version}, #{log.patch}, #{log.full})")
    void newVersionSave(@Param("log") final VersionLog log);

    @Select("SELECT * FROM version_log ORDER BY id DESC LIMIT 1")
    VersionLog latestVersion();

    @Select("SELECT id FROM version_log WHERE version = #{version}")
    int getVersionId(@Param("version") String version);

    @Select("SELECT * FROM version_log WHERE id > #{versionId}")
    List<VersionLog> getUpdateVersionList(@Param("versionId") int versionId);

    @Select("SELECT * FROM version_log")
    List<VersionLog> getAllVersionList();

}
