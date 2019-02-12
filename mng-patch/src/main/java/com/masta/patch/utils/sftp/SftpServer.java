package com.masta.patch.utils.sftp;

import com.jcraft.jsch.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.*;

@Slf4j
@Component
public class SftpServer {

    @Value("${sftp.url}")
    private String url;

    @Value("${sftp.user}")
    private String user;

    @Value("${sftp.password}")
    private String password;

    private Session session = null;
    private Channel channel = null;
    private ChannelSftp channelSftp = null;

    // SFTP 서버연결
    public void init() {

        //JSch 객체 생성
        JSch jsch = new JSch();
        try {
            //세션객체 생성 ( user , host, port )
            session = jsch.getSession(user, url);

            //password 설정
            session.setPassword(password);

            //세션관련 설정정보 설정
            java.util.Properties config = new java.util.Properties();

            //호스트 정보 검사하지 않는다.
            config.put("StrictHostKeyChecking", "no");
            session.setConfig(config);

            //접속
            session.connect();

            //sftp 채널 접속
            channel = session.openChannel("sftp");
            channel.connect();

        } catch (JSchException e) {
            e.printStackTrace();
        }
        channelSftp = (ChannelSftp) channel;

    }

    // 단일 파일 업로드
    public void upload(String dir, String path) {
        File file = new File(path);
        FileInputStream in = null;

        try {
            in = new FileInputStream(file);
            channelSftp.cd(dir);
            channelSftp.put(in, file.getName());
        } catch (SftpException se) {
            se.printStackTrace();
        } catch (FileNotFoundException fe) {
            fe.printStackTrace();
        } finally {
            try {
                in.close();
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
        }
    }

    // 단일 파일 다운로드
    public InputStream download(String dir, String fileNm) {
        InputStream in = null;
        String path = "...";
        try { //경로탐색후 inputStream에 데이터를 넣음
            channelSftp.cd(path + dir);
            in = channelSftp.get(fileNm);

        } catch (SftpException se) {
            se.printStackTrace();
        }

        return in;
    }

    public void backupDir(String srcDir, String backPath) {
        try {
            channelSftp.rmdir(backPath);
            //log.info(channelSftp.ls("\\gameFiles").toString());
            channelSftp.rename(srcDir, backPath);
            channelSftp.mkdir(srcDir);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    public void uploadDir(File localFile, String destPath) {
        try {
            if (localFile.isDirectory()) {
                channelSftp.mkdir(destPath);
                //System.out.println("Created Folder: " + localFile.getName() + " in " + destPath);

                destPath = destPath + "/" + localFile.getName();
                channelSftp.cd(destPath);

                for (File file : localFile.listFiles()) {
                    uploadDir(file, destPath);
                }

                channelSftp.cd(destPath.substring(0, destPath.lastIndexOf('/')));
            } else {
                System.out.println("Copying File: " + localFile.getName() + " to " + destPath);
                channelSftp.put(new FileInputStream(localFile), localFile.getName(), ChannelSftp.OVERWRITE);
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }

    }

    // 파일서버와 세션 종료
    public void disconnect() {
        channelSftp.quit();
        session.disconnect();
    }

}