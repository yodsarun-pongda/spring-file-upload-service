package com.yodsarun.demo.spring.interview.service;

import com.yodsarun.demo.spring.interview.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.io.File;

@Service
@Slf4j
public class CloudStorageService {
    public boolean uploadFileToStorage(String fileToUploadPath) {
        try {
            if (StringUtils.isEmpty(fileToUploadPath)) {
                throw new BusinessException("Can not upload file to cloud storage, File path is invalid", HttpStatus.INTERNAL_SERVER_ERROR);
            }

            log.info("process to upload file to cloud e.g. S3, AWS, GCP, etc.");
            var file = new File("fileToUploadPath");
            return true;
        } catch (BusinessException e) {
            log.error("Error occurred when upload file to cloud storage", e);
            throw e;
        }
    }
}
