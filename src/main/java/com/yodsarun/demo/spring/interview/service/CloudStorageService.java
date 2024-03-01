package com.yodsarun.demo.spring.interview.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.File;

@Service
@Slf4j
@RequiredArgsConstructor
public class CloudStorageService {
    public boolean uploadFileToStorage(File fileToUpload) {
        log.info("process to upload file to cloud e.g. S3, AWS, GCP, etc.");
        return true;
    }
}
