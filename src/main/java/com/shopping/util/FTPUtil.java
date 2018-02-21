package com.shopping.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.net.ftp.FTPClient;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

/**
 * Created by wang on 2017/5/15.
 */
@Slf4j
public class FTPUtil {

    private static String ftpIp = PropertiesUtil.getProperty("ftp.server.ip", "123.206.211.185");

    private static String ftpUser = PropertiesUtil.getProperty("ftp.user", "ftpuser");

    private static String ftpPassword = PropertiesUtil.getProperty("ftp.pass", "123456");

    private String ip;

    private int port;

    private String user;

    private String password;

    private FTPClient ftpClient;

    public FTPUtil(String ip, int port, String user, String password) {
        this.ip = ip;
        this.port = port;
        this.user = user;
        this.password = password;
    }

    public static boolean uploadFile(List<File> fileList) throws IOException {
        FTPUtil ftpUtil = new FTPUtil(ftpIp, 21, ftpUser, ftpPassword);
        log.info("-------开始上传文件-----");
        boolean res = ftpUtil.uploadFile("img", fileList);
        log.info("结束上传，上传结果：");

        return res;
    }

    public boolean uploadFile(String remotePath, List<File> fileList) throws IOException {
        boolean uploaded = true;
        FileInputStream fileInputStream = null;
        //链接到服务器
        if (connectFtpServer(this.ip, this.port, this.user, this.password)) {
            try {
                //更改工作目录
                ftpClient.changeWorkingDirectory(remotePath);
                ftpClient.setBufferSize(1024);
                ftpClient.setControlEncoding("UTF-8");
                ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);
                ftpClient.enterLocalPassiveMode();
                for (File file : fileList) {
                    fileInputStream = new FileInputStream(file);
                    ftpClient.storeFile(file.getName(), fileInputStream);
                }
            } catch (IOException e) {
                uploaded = false;
                log.error("上传文件异常:" + e);
                e.printStackTrace();
            } finally {
                fileInputStream.close();
                ftpClient.disconnect();
            }

            return uploaded;
        }

        return false;
    }

    private boolean connectFtpServer(String ip, int port, String username, String password) {
        ftpClient = new FTPClient();
        boolean isSuccessConnect = false;
        try {
            ftpClient.connect(ip, port);
            isSuccessConnect = ftpClient.login(username, password);
        } catch (IOException e) {
            log.error("链接服务器异常:" + e);
        }

        return isSuccessConnect;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public FTPClient getFtpClient() {
        return ftpClient;
    }

    public void setFtpClient(FTPClient ftpClient) {
        this.ftpClient = ftpClient;
    }
}
