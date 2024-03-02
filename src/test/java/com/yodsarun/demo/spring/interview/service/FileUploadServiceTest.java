package com.yodsarun.demo.spring.interview.service;

import com.yodsarun.demo.spring.interview.config.app.UploadFileConfiguration;
import com.yodsarun.demo.spring.interview.entity.CustomerEntity;
import com.yodsarun.demo.spring.interview.exception.BusinessException;
import com.yodsarun.demo.spring.interview.repository.CustomerRepository;
import com.yodsarun.demo.spring.interview.service.impl.FileUploadServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@Slf4j
@ExtendWith(MockitoExtension.class)
public class FileUploadServiceTest {
    @InjectMocks
    FileUploadServiceImpl fileUploadService;

    @Mock
    EmailService emailService;
    @Mock
    CloudStorageService cloudStorageService;
    @Mock
    CustomerRepository customerRepository;

    private static final MultipartFile file = new MockMultipartFile("data", "filename.txt", "text/plain", "some xml".getBytes());
    private static final String customerName = "name";
    private static final String email = "email@domain.com";

    @BeforeEach
    void setUp() {
        var uploadFileConfiguration = new UploadFileConfiguration();
        uploadFileConfiguration.setAllowExtension("txt,csv,text");
        uploadFileConfiguration.setMailSubject("subject");
        uploadFileConfiguration.setMailTemplate("template.html");
        uploadFileConfiguration.setSavePath("src/main/resources/files");
        ReflectionTestUtils.setField(fileUploadService, "uploadFileConfiguration", uploadFileConfiguration);
    }

    @Test
    void uploadFileToCloudStorageThenExpectSuccess() {
        when(customerRepository.findCustomerEntityByName(eq(customerName))).thenReturn(mockCustomerEntity());
        when(cloudStorageService.uploadFileToStorage(any())).thenReturn(true);
        doNothing().when(emailService).sentEmail(any());

        fileUploadService.uploadFileAndSendEmail(file, customerName);

        verify(customerRepository, times(1)).findCustomerEntityByName(any());
        verify(cloudStorageService, times(1)).uploadFileToStorage(any());
        verify(emailService, times(1)).sentEmail(any());
    }

    @Test
    void givenRequestFileIsNotAllowedWhenUploadFileToCloudStorageThenExpectSuccess() {
        var mockFile = new MockMultipartFile("data", "filename.png", "image", "some xml".getBytes());
        var actualException = Assertions.assertThrows(BusinessException.class, () -> {
            fileUploadService.uploadFileAndSendEmail(mockFile, customerName);
        });

        Assertions.assertEquals("Request File extension is not allowed", actualException.getMessage());
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, actualException.getHttpStatus());
        verify(customerRepository, never()).findCustomerEntityByName(any());
        verify(cloudStorageService, never()).uploadFileToStorage(any());
        verify(emailService, never()).sentEmail(any());
    }

    @Test
    void givenCanNotConnectToDbWhenUploadFileToCloudStorageThenExpectSuccess() {
        when(customerRepository.findCustomerEntityByName(eq(customerName))).thenThrow(new DataAccessResourceFailureException("some error"));

        var actualException = Assertions.assertThrows(BusinessException.class, () -> {
            fileUploadService.uploadFileAndSendEmail(file, customerName);
        });

        Assertions.assertEquals("DataAccessResourceFailureException: some error", actualException.getMessage());
        Assertions.assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, actualException.getHttpStatus());
        verify(customerRepository, times(1)).findCustomerEntityByName(any());
        verify(cloudStorageService, never()).uploadFileToStorage(any());
        verify(emailService, never()).sentEmail(any());
    }

    @Test
    void givenCanNotSendEmailWhenUploadFileToCloudStorageThenExpectSuccess() {
        when(customerRepository.findCustomerEntityByName(eq(customerName))).thenReturn(mockCustomerEntity());
        when(cloudStorageService.uploadFileToStorage(any())).thenReturn(true);
        doThrow(new BusinessException("some error", HttpStatus.INTERNAL_SERVER_ERROR)).when(emailService).sentEmail(any());

        fileUploadService.uploadFileAndSendEmail(file, customerName);

        verify(customerRepository, times(1)).findCustomerEntityByName(any());
        verify(cloudStorageService, times(1)).uploadFileToStorage(any());
        verify(emailService, times(1)).sentEmail(any());
    }

    @Test
    void givenNotFoundCustomerOnDbWhenUploadFileToCloudStorageThenExpectFail() {
        when(customerRepository.findCustomerEntityByName(eq(customerName))).thenReturn(null);

        Assertions.assertThrows(BusinessException.class, () -> {
            fileUploadService.uploadFileAndSendEmail(file, customerName);
        });

        verify(customerRepository, times(1)).findCustomerEntityByName(any());
        verify(cloudStorageService, never()).uploadFileToStorage(any());
        verify(emailService, never()).sentEmail(any());
    }

    @Test
    void givenCustomerNameRequestIsMissingWhenUploadFileToCloudStorageThenExpectFail() {
        var actualException = Assertions.assertThrows(BusinessException.class, () -> {
            fileUploadService.uploadFileAndSendEmail(file, StringUtils.EMPTY);
        });

        Assertions.assertEquals("Request property name is missing", actualException.getMessage());
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, actualException.getHttpStatus());
        verify(customerRepository, never()).findCustomerEntityByName(any());
        verify(cloudStorageService, never()).uploadFileToStorage(any());
        verify(emailService, never()).sentEmail(any());
    }

    @Test
    void givenFileRequestIsMissingWhenUploadFileToCloudStorageThenExpectFail() {
        var actualException = Assertions.assertThrows(BusinessException.class, () -> {
            fileUploadService.uploadFileAndSendEmail(null, customerName);
        });

        Assertions.assertEquals("Request file is missing", actualException.getMessage());
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, actualException.getHttpStatus());
        verify(customerRepository, never()).findCustomerEntityByName(any());
        verify(cloudStorageService, never()).uploadFileToStorage(any());
        verify(emailService, never()).sentEmail(any());
    }

    private CustomerEntity mockCustomerEntity() {
        var mockCustomerEntity = new CustomerEntity();
        mockCustomerEntity.setEmail(email);
        mockCustomerEntity.setName(customerName);
        return mockCustomerEntity;
    }
}
