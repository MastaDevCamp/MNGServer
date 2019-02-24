package com.masta.patch.service;

import com.masta.core.response.DefaultRes;
import com.masta.core.response.ResponseMessage;
import com.masta.core.response.StatusCode;
import com.masta.patch.dto.VersionLog;
import com.masta.patch.mapper.VersionMapper;
import com.masta.patch.model.DirEntry;
import com.masta.patch.utils.JsonMaker.FullJsonMaker;
import com.masta.patch.utils.JsonMaker.PatchJsonMaker;
import com.masta.patch.utils.TypeConverter;
import com.masta.patch.utils.SftpServer;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.io.File;
import java.util.List;

import static com.masta.patch.model.VersionCheckResultType.checkRightVersion;
import static com.masta.patch.utils.Compress.unzip;
import static com.masta.patch.utils.LocalFileIO.resetDir;


@Slf4j
@Service
public class UpdateService {

    final SftpServer sftpServer;
    final FullJsonMaker fullJsonMaker;
    final PatchJsonMaker patchJsonMaker;
    final VersionMapper versionMapper;
    final TypeConverter typeConverter;

    @Value("${local.newVersion.path}")
    private String newVersionPath;

    @Value("${local.merge.path}")
    private String mergePath;

    @Value("${local.path}")
    private String localPath;

    @Value("${sftp.root.path}")
    private String sftpRootPath;

    @Value("${local.verUpZip.path}")
    public String verUpZipPath;

    @Value("${local.zipFile.path}")
    public String zipFilePath;


    public UpdateService(final SftpServer sftpServer, final FullJsonMaker fullJsonMaker,
                         final PatchJsonMaker patchJsonMaker, final VersionMapper versionMapper,
                         final TypeConverter typeConverter) {
        this.sftpServer = sftpServer;
        this.fullJsonMaker = fullJsonMaker;
        this.patchJsonMaker = patchJsonMaker;
        this.versionMapper = versionMapper;
        this.typeConverter = typeConverter;
    }

    public File saveLocal(MultipartFile sourceFile) {
        resetDir(newVersionPath);
        File file = new File(newVersionPath + sourceFile.getName() + ".zip");
        try {
            sourceFile.transferTo(file);
            log.info("save file [" + file.getPath() + "]");
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return file;
    }


    ////////////////////////////////////////////////////////////////////////////////////////////

    @Transactional
    public DefaultRes updateVersion(MultipartFile newVersionFile, String newVersion) {
        try {
            // 1. 업로드 파일 zip인지 확인
            if (!checkFileType(newVersionFile, "zip")) {
                return DefaultRes.res(StatusCode.NOT_FORMAT, ResponseMessage.NOT_ZIP_FILE);
            }

            // 2. 입력 받은 버전이 포멧하고 최신인지 확인
            String latestVersion = versionMapper.latestVersion().getVersion();
            switch (checkRightVersion(newVersion, latestVersion)) {
                case NOT_LATEST_VERSION:
                    return DefaultRes.res(StatusCode.NOT_FORMAT, ResponseMessage.NOT_ZIP_FILE);
                case NOT_VERSION_FORMAT:
                    return DefaultRes.res(StatusCode.NOT_FORMAT, ResponseMessage.NOT_VERSION_FORMAT);
                case LATEST_VERSION:
                    break;
            }

            // 3. 패치 만들기(FULL JSON, PATCH JSON, PATCH FILES)
            makePatch(newVersionFile, newVersion);

            // 4. 업로드 하기(Patch_Ver_[newVersion].json, Full_Ver_[newVersion].json, Patch Files to file/history/[newVersion], Full Files to file/release)
            UploadPatch(newVersion);

            return DefaultRes.res(StatusCode.OK, ResponseMessage.SUCCESS_TO_NEW_VERSION);
        } catch (Exception e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            log.error(e.getMessage());
            return DefaultRes.res(StatusCode.INTERNAL_SERVER_ERROR, ResponseMessage.DB_ERROR);
        }


    }

    ///////////////////////////////////            // 4. 업로드 하기(Patch_Ver_[newVersion].json, Full_Ver_[newVersion].json, Patch Files to file/history/[newVersion], Full Files to file/release)
    public void UploadPatch(String newVersion) {

        UploadJson(newVersion);

        List<String> fullFileList = typeConverter.fullJsonToFileList(String.format("Full_Ver_%s.json", newVersion));
        uploadToRemote(fullFileList, "file/release");

        List<String> patchFileList = typeConverter.patchJsonToFileList(String.format("Patch_Ver_%s.json", newVersion));
        uploadToRemote(patchFileList, "file/history/" + newVersion);

    }

    public void uploadToRemote(List<String> uploadFileList, String remotePath) {
        sftpServer.init();
        sftpServer.rmDir(remotePath);
        sftpServer.mkdir(remotePath);

        for (String patchFile : uploadFileList) {
            String[] fileInfo = patchFile.replace(" ", "").split("\\|");

            if ("D".equals(fileInfo[0])) {  // not dir
                continue;
            }

            if (!"D".equals(fileInfo[8])) {     // Upload only Update, create patch file
                String relativePath = fileInfo[1].replace("\\", "/").substring(0, fileInfo[1].lastIndexOf("\\"));
                sftpServer.mkdir(relativePath, remotePath);
                sftpServer.upload(new File(zipFilePath + fileInfo[1] + "." + fileInfo[2]), remotePath + relativePath);
            }

        }

        sftpServer.disconnect();
    }


    public void UploadJson(String newVersion) {
        File fullJsonFile = new File(localPath + String.format("Full_Ver_%s.json", newVersion));
        File patchJsonFile = new File(localPath + String.format("Patch_Ver_%s.json", newVersion));

        try {
            sftpServer.upload(fullJsonFile, "log/full");
            sftpServer.upload(patchJsonFile, "log/patch");
        } catch (Exception e) {
            log.info("Upload full and patch json");
        }

        String remoteFullJsonPath = sftpServer.checkFile(newVersion, "log/full");
        String remotePatchJsonPath = sftpServer.checkFile(newVersion, "log/patch");

        // DB 저장
        VersionLog newVersionLog = VersionLog.builder()
                .version(newVersion)
                .full(remoteFullJsonPath)
                .patch(remotePatchJsonPath).build();

        versionMapper.newVersionSave(newVersionLog);
    }


    ////////////////////////////// // 3. 패치 만들기(FULL JSON, PATCH JSON, PATCH FILES)
    public void makePatch(MultipartFile newVersionFile, String newVersion) {
        DirEntry latestVersionFileTree = getVersionFileTree();

        DirEntry newVersionFileTree = getVersionFileTree(newVersionFile, newVersion);
        typeConverter.saveJsonFile(newVersionFileTree, String.format("Full_Ver_%s.json", newVersion));

        List<String> newVersionPatchFileList = patchJsonMaker.getPatchFileList(newVersionFileTree, latestVersionFileTree);
        typeConverter.saveJsonFile(newVersionPatchFileList, String.format("Patch_Ver_%s.json", newVersion));

    }


    public DirEntry getVersionFileTree(MultipartFile newVersionFile, String version) {
        File newVersionZip = saveLocal(newVersionFile);
        String unzipPath = unzip(newVersionZip);
        return fullJsonMaker.getVersionFileTree(unzipPath, version);
    }

    public DirEntry getVersionFileTree() {
        return typeConverter.getRemoteLatestVersionJson();
    }


    ////////////////////////////// 2. 버전이 맞는지 확인(포멧하고 최신 버전인지)

    //이거 version Util로 빼도 되는지?! (updatett에서도 사용함함)

   //version checking (util에서 확인!)

    ////////////////////////////// 1. 업로드 받은 파일이 zip파일인지 확인
    public boolean checkFileType(MultipartFile sourceFile, String fileType) {
        String sourceFileName = sourceFile.getOriginalFilename();
        return fileType.equals(FilenameUtils.getExtension(sourceFileName).toLowerCase());
    }


    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////


}