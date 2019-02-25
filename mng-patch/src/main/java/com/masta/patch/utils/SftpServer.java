package com.masta.patch.utils;

import com.jcraft.jsch.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Vector;

@Slf4j
@Component
public class SftpServer {

    @Value("${local.merge.path}")
    private String localMergePath;

    @Value("${sftp.url}")
    private String url;

    @Value("${sftp.user}")
    private String user;

    @Value("${sftp.password}")
    private String password;

    @Value("${sftp.root.path}")
    private String rootPath;

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
        try {
            channelSftp = (ChannelSftp) channel;
            channelSftp.cd(rootPath);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    // 단일 파일 업로드
    public void upload(File file, String dir) {
        FileInputStream in = null;

        try {
            in = new FileInputStream(file);
            channelSftp.cd(rootPath + dir);
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

    public void mkdir(String path) {
        try {
            channelSftp.mkdir(rootPath + path);

        } catch (Exception e) {
            log.error(e.getMessage());
        }

    }

    public void mkdir(String path, String parentPath) {
        if ("".equals(path)) return;
        try {
            channelSftp.cd(rootPath + parentPath);
            String[] dirs = path.split("/");
            for (String dir : dirs) {
                if ("".equals(dir)) continue;
                channelSftp.mkdir(dir);
                channelSftp.cd(dir);
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    public void backupDir(String srcDir, String destinationDir) {
        try {
            channelSftp.rename(rootPath + srcDir, rootPath + destinationDir);
            channelSftp.mkdir(rootPath + srcDir);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    public void rmDir(String srcDir) {
        recursiveRmDir(rootPath + srcDir);
    }

    public void remove(String path) {
        try {
            channelSftp.rm(rootPath + path);
        } catch (Exception e) {
            log.error(e.getMessage());
        }

    }


    public void recursiveRmDir(String path) {
        try {
            channelSftp.cd(path);
            Vector<ChannelSftp.LsEntry> fileAndFolderList = channelSftp.ls(path);
            for (ChannelSftp.LsEntry item : fileAndFolderList) {

                if (!item.getAttrs().isDir()) {
                    channelSftp.rm(path + "/" + item.getFilename()); // Remove file.
                } else if (!(".".equals(item.getFilename()) || "..".equals(item.getFilename()))) {
                    try {
                        channelSftp.rmdir(path + "/" + item.getFilename());

                    } catch (Exception e) {
                        recursiveRmDir(path + "/" + item.getFilename());
                    }
                }
            }

            channelSftp.rmdir(path); // delete the parent directory after empty
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    public void uploadDir(File localDir, String destPath) {
        try {
            if (localDir.isDirectory()) {
                channelSftp.mkdir(destPath);

                destPath = destPath + "/" + localDir.getName();
                channelSftp.cd(destPath);

                for (File file : localDir.listFiles()) {
                    uploadDir(file, destPath);
                }

                channelSftp.cd(destPath.substring(0, destPath.lastIndexOf('/')));
            } else {
                channelSftp.put(new FileInputStream(localDir), localDir.getName(), ChannelSftp.OVERWRITE);
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }

    }


    public String checkFile(String name, String path) {
        try {
            Vector<ChannelSftp.LsEntry> list = channelSftp.ls(rootPath + path);
            for (ChannelSftp.LsEntry entry : list) {
                if (entry.getFilename().contains(name)) {
                    return path + "/" + entry.getFilename();
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return "";
    }

    // 파일서버와 세션 종료
    public void disconnect() {
        channelSftp.quit();
        session.disconnect();
    }

}