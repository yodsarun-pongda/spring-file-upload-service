package com.yodsarun.demo.spring.interview.controller;

import com.yodsarun.demo.spring.interview.model.base.ResponseModel;
import com.yodsarun.demo.spring.interview.model.files.UploadFileResponse;
import com.yodsarun.demo.spring.interview.service.FileUploadService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/v1")
@RequiredArgsConstructor
public class FileUploadController {
    private final FileUploadService fileUploadService;

    @PostMapping(value = "/upload/file", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResponseModel<UploadFileResponse>> uploadFileController(@RequestParam(value = "file") MultipartFile file,
                                                                                  @RequestParam(value = "name") String name) {
        // @valid can be use in this case but, i want to custom response message
        return ResponseEntity.ok().body(fileUploadService.uploadFileAndSendEmail(file, name));
    }
}
