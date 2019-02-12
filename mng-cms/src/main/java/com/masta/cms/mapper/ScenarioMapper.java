package com.masta.cms.mapper;


import com.masta.cms.dto.Choice;
import com.masta.cms.dto.Perform;
import com.masta.cms.model.PerformReq;
import com.masta.cms.model.SceneReq;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface ScenarioMapper {
    ///////////////GET/////////////////
    //수행한 모든 시나리오 조회 (type 별 전체)
    @Select("SELECT * FROM perform WHERE type=#{type} AND uid=#{uid}")
    List<Perform> findAllPerformedWithType(@Param("type") int type, @Param("uid") int uid);

    //최근에 수행한 시나리오 조회 (각 타입 별)
    @Select("SELECT * FROM perform WHERE (type, prfm_at) IN (SELECT type, max(prfm_at) FROM perform WHERE uid=#{uid} GROUP BY type)")
    List<Perform> findPerformedByType(@Param("uid") int uid);

    //최근에 수행한 시나리오 조회 (딱 하나)
    @Select("SELECT * FROM perform WHERE (type, prfm_at) IN (SELECT type, max(prfm_at) FROM perform WHERE uid=#{uid}) LIMIT 1")
    Perform findOnePerformedLately(@Param("uid") int uid);

    //타입 챕터 씬으로 prfm_id 조회
    @Select("SELECT prfm_id FROM perform WHERE uid=#{sceneReq.uid} AND type=#{sceneReq.type} AND chapter=#{sceneReq.chapter} AND scene=#{sceneReq.scene}")
    int findPerformIdWithSceneReq(@Param("sceneReq") SceneReq sceneReq);


    //시나리오 스크립트 답변 조회 (스크립트 별)
    //스크립트가 유니크한가??
//    @Select("SELECT * FROM choice WHERE script=#{script} AND uid=#{uid}")
//    Choice findAnswerWithScript(@Param("script") int script, @Param("uid") int uid);

    //시나리오 스크립트 답변 조회 (타입 챕터 신 별)
    @Select("SELECT * FROM choice WHERE prfm_id IN (SELECT prfm_id FROM perform WHERE uid=#{sceneReq.uid} AND type=#{sceneReq.type} AND chapter=#{sceneReq.chapter} AND scene=#{sceneReq.scene})")
    List<Choice> findScriptChoice(@Param("sceneReq") SceneReq sceneReq);

    //수행 시나리오 중복 확인
    @Select("SELECT * FROM perform WHERE uid=#{perform.uid} AND type=#{perform.type} AND chapter=#{perform.chapter} AND scene=#{perform.scene}")
    List<Perform> findDuplicatedPerformedScene(@Param("perform") PerformReq performReq);

    ///////////////POST/////////////////
    //시나리오 시작
    @Insert("INSERT INTO perform (uid, type, chapter, scene, prgs) VALUES (#{perform.uid}, #{perform.type}, #{perform.chapter}, #{perform.scene}, #{perform.prgs})")
    void insertPerformStarted(@Param("perform") PerformReq perform);

    //시나리오 질문 선택
    @Insert("INSERT INTO choice (prfm_id, script, answer) VALUES (#{prfm_id}, #{script}, #{answer})")
    void inserChoice(@Param("prfm_id") int prfm_id, @Param("script") int script, @Param("answer") int answer);


    ///////////////PUT/////////////////
    //시나리오 끝
    @Update("UPDATE perform SET prgs = 1 WHERE uid=#{perform.uid} AND type=#{perform.type} AND chapter=#{perform.chapter} AND scene=#{perform.scene}")
    void updatePerformFinished(@Param("perform") PerformReq perform);


    ///////////////DELETE/////////////////
}
