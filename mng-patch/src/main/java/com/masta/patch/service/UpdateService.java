package com.masta.patch.service;

import com.masta.core.response.DefaultRes;
import com.masta.core.response.ResponseMessage;
import com.masta.core.response.StatusCode;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;


@Slf4j
@Service
public class UpdateService {

    @Autowired
    private VersionService versionService;
    @Autowired
    private PreparePatchService preparePatchService;
    @Autowired
    private UploadPatchService uploadPatchService;


    @Value("${local.path}")
    private String localPath;

    @Transactional
    public DefaultRes updateVersion(MultipartFile newVersionFile, String newVersion) {
        try {
            // 1. 업로드 파일 zip인지 확인
            if (!checkFileType(newVersionFile, "zip")) {
                return DefaultRes.res(StatusCode.NOT_FORMAT, ResponseMessage.NOT_ZIP_FILE);
            }

            // 2. 입력 받은 버전이 포멧하고 최신인지 확인
            String checkRightVersionResult = versionService.checkRightVersion(newVersion);
            if (checkRightVersionResult != ResponseMessage.SUCCESS_TO_GET_LATEST_VERSION) {
                return DefaultRes.res(StatusCode.NOT_FORMAT, checkRightVersionResult);
            }

            // 3. 패치 만들기(FULL JSON, PATCH JSON, PATCH FILES)
            String makePatchResult = preparePatchService.makePatch(newVersionFile, newVersion);
            if (makePatchResult != ResponseMessage.SUCCESS_TO_SAVE_JSON_IN_LOCAL) {
                return DefaultRes.res(StatusCode.INTERNAL_SERVER_ERROR, checkRightVersionResult);
            }

            // 4. 업로드 하기(Patch_Ver_[newVersion].json, Full_Ver_[newVersion].json, Patch Files to file/history/[newVersion], Full Files to file/release)
            String uploadPatchResult = uploadPatchService.uploadPatch(newVersion);
            if (uploadPatchResult != ResponseMessage.SUCCESS_TO_UPLOAD_PATCH) {
                return DefaultRes.res(StatusCode.INTERNAL_SERVER_ERROR, uploadPatchResult);
            }

            return DefaultRes.res(StatusCode.OK, ResponseMessage.SUCCESS_TO_NEW_VERSION);
        } catch (Exception e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            log.error(e.getMessage());
            uploadPatchService.initSftpServer(newVersion);
            return DefaultRes.res(StatusCode.INTERNAL_SERVER_ERROR, ResponseMessage.DB_ERROR);
        }
    }

    public boolean checkFileType(MultipartFile sourceFile, String fileType) {
        String sourceFileName = sourceFile.getOriginalFilename();
        return fileType.equals(FilenameUtils.getExtension(sourceFileName).toLowerCase());
    }



}