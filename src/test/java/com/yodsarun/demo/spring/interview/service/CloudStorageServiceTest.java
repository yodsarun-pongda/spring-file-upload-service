package com.yodsarun.demo.spring.interview.service;

import com.yodsarun.demo.spring.interview.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;


@Slf4j
@ExtendWith(MockitoExtension.class)
public class CloudStorageServiceTest {
    @InjectMocks
    CloudStorageService cloudStorageService;

    @Test
    void givenUploadFileToCloudThenExpectSuccess() {
        cloudStorageService.uploadFileToStorage("/path/To/File.txt");
    }

    @Test
    void givenCanNotOpenFileWhenUploadFileToCloudThenExpectFailed() {

        var actualException = Assertions.assertThrows(BusinessException.class, () -> {
            cloudStorageService.uploadFileToStorage(null);
        });

        Assertions.assertEquals("Can not upload file to cloud storage, File path is invalid", actualException.getMessage());
    }
}
