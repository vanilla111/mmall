package com.shopping.service.impl;

import com.google.common.collect.Lists;
import com.shopping.service.IFileService;
import com.shopping.util.FTPUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

/**
 * Created by wang on 2017/5/15.
 */
@Service("iFileService")
@Slf4j
public class FileServiceImpl implements IFileService {

    public String upload(MultipartFile file, String path) {
        String fileName = file.getOriginalFilename();
        String fileExtensionName = fileName.substring(fileName.lastIndexOf(".") + 1);

        String uploadFileName = UUID.randomUUID().toString() + "." + fileExtensionName;
        log.info("开始上传文件，上传的文件名:{}，上传的路径:{}，新文件名:{}", fileName, path, uploadFileName);

        File fileDir = new File(path);
        if (!fileDir.exists()) {
            fileDir.setWritable(true);
            fileDir.mkdirs();
        }

        File targetFile = new File(path, uploadFileName);
        try {
            //临时保存文件在上传文件夹下
            file.transferTo(targetFile);
            //上传到FTP服务器上
            FTPUtil.uploadFile(Lists.newArrayList(targetFile));
            //删除临时文件
            targetFile.delete();
        } catch (IOException e) {
            log.error("上传文件异常");
            return null;
        }

        return targetFile.getName();
    }
}
