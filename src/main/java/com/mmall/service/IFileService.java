package com.mmall.service;

import org.springframework.web.multipart.MultipartFile;

/**
 * Created by wang on 2017/5/15.
 */
public interface IFileService {
    String upload(MultipartFile file, String path);
}
