package com.yodsarun.demo.spring.interview.service;

import com.yodsarun.demo.spring.interview.model.base.ResponseModel;
import com.yodsarun.demo.spring.interview.model.files.UploadFileResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

public interface FileUploadService {
    ResponseModel<UploadFileResponse> uploadFileAndSendEmail(MultipartFile multipartFile,
                                                             String name);
}
