package com.masta.cms.service;

import com.masta.cms.dto.Choice;
import com.masta.cms.dto.Perform;
import com.masta.cms.mapper.ScenarioMapper;
import com.masta.cms.model.PerformReq;
import com.masta.cms.model.SceneReq;
import com.masta.core.response.DefaultRes;
import com.masta.core.response.ResponseMessage;
import com.masta.core.response.StatusCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.util.List;

@Slf4j
@Service
public class ScenarioService {
    private final ScenarioMapper scenarioMapper;
    public ScenarioService(final ScenarioMapper scenarioMapper) { this.scenarioMapper = scenarioMapper; }

    //타입별로 최근 시나리오 찾기
    public DefaultRes getScenarioByType(final int uid) {
        try {
            List<Perform> performs = scenarioMapper.findPerformedByType(uid);
            return DefaultRes.res(StatusCode.OK, ResponseMessage.FIND_SCENARIO, performs);
        } catch (Exception e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            log.error(e.getMessage());
            return DefaultRes.res(StatusCode.DB_ERROR, ResponseMessage.DB_ERROR);
        }
    }

    //시나리오 정보로 답변 찾기
    public DefaultRes getScriptAnsWithSceneReq(final SceneReq sceneReq) {
        try {
            log.info("안뇽하쇼 : " + sceneReq);
            List<Choice> choice = scenarioMapper.findScriptChoice(sceneReq);
            return DefaultRes.res(StatusCode.OK, ResponseMessage.FIND_SCENARIO_ANSWER, choice);
        } catch (Exception e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            log.error(e.getMessage());
            return DefaultRes.res(StatusCode.DB_ERROR, ResponseMessage.DB_ERROR);
        }
    }

    //답변 등록
    public DefaultRes postChoice(final SceneReq sceneReq) {
        try {
            SceneReq findScript = new SceneReq();
            findScript.setType(sceneReq.getType());
            findScript.setChapter(sceneReq.getChapter());
            findScript.setScene(sceneReq.getScene());
            findScript.setUid(sceneReq.getUid());
            int prfm_id = scenarioMapper.findPerformIdWithSceneReq(findScript);
            log.info("prfm_id : " + prfm_id);
            scenarioMapper.inserChoice(prfm_id, sceneReq.getScript(), sceneReq.getAnswer());
            return DefaultRes.res(StatusCode.OK, ResponseMessage.REGISTER_SCENARIO_ANSWER);
        } catch (Exception e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            log.error(e.getMessage());
            return DefaultRes.res(StatusCode.DB_ERROR, ResponseMessage.DB_ERROR);
        }
    }

    //시나리오 시작/종료
    public DefaultRes editPrgsScenario(final int uid, final int type, final int ch, final int sc, final int prgs) {
        try {
            PerformReq performReq = new PerformReq();
            performReq.setUid(uid);
            performReq.setType(type);
            performReq.setChapter(ch);
            performReq.setScene(sc);
            performReq.setPrgs(prgs);
            if (prgs == 0) {
                List<Perform> duplicatedPerform = scenarioMapper.findDuplicatedPerformedScene(performReq);
                if (duplicatedPerform == null) {
                    scenarioMapper.insertPerformStarted(performReq);
                    return DefaultRes.res(StatusCode.OK, ResponseMessage.MODIFY_SCENARIO_PROGRESS);
                }
                else {
                    return DefaultRes.res(StatusCode.OK, ResponseMessage.ALREADY_SCENARIO_START);
                }
            }
            else {
                scenarioMapper.updatePerformFinished(performReq);
                return DefaultRes.res(StatusCode.OK, ResponseMessage.MODIFY_SCENARIO_PROGRESS);
            }
        } catch (Exception e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            log.error(e.getMessage());
            return DefaultRes.res(StatusCode.DB_ERROR, ResponseMessage.DB_ERROR);
        }
    }
}
