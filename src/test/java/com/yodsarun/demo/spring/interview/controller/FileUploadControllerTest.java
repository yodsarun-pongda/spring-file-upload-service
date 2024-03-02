package com.yodsarun.demo.spring.interview.controller;

import com.yodsarun.demo.spring.interview.exception.AdviceHandler;
import com.yodsarun.demo.spring.interview.exception.BusinessException;
import com.yodsarun.demo.spring.interview.model.base.ResponseModel;
import com.yodsarun.demo.spring.interview.model.files.UploadFileResponse;
import com.yodsarun.demo.spring.interview.service.FileUploadService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.multipart.MultipartFile;

import java.nio.charset.StandardCharsets;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@Slf4j
@WebMvcTest(FileUploadController.class)
@Import(AdviceHandler.class)
@ContextConfiguration(classes = {FileUploadController.class})
public class FileUploadControllerTest {
    @Autowired
    MockMvc mockMvc;

    @MockBean
    FileUploadService fileUploadService;

    @Autowired
    private WebApplicationContext webApplicationContext;

    private static final String authKey = "authKey";

    private static final MultipartFile file = new MockMultipartFile("data", "filename.txt", "text/plain", "some xml".getBytes());
    private static final String customerName = "name";
    private static final String UPLOAD_FILE_URL = "/v1/upload/file";

    @Captor
    ArgumentCaptor<MultipartFile> captorMultipartFile;

    @Test
    void uploadFileWithAllRequiredFieldsThenExpectSuccess() throws Exception {
        when(fileUploadService.uploadFileAndSendEmail(file, customerName)).thenReturn(mockResponseModel());

        MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();

        mockMvc.perform(MockMvcRequestBuilders.multipart(UPLOAD_FILE_URL)
                    .file("file", file.getBytes()).characterEncoding(StandardCharsets.UTF_8)
                    .param("name", customerName)
                    .header("authKey", authKey)
                )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    void givenMissingFileRequestThenExpectFail() throws Exception {
        doThrow(new BusinessException("Request file is missing", HttpStatus.BAD_REQUEST))
                .when(fileUploadService).uploadFileAndSendEmail(any(), any());

        mockMvc.perform(MockMvcRequestBuilders.multipart(UPLOAD_FILE_URL)
                        .param("name", customerName)
                        .header("authKey", authKey)
                )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isBadRequest());

        verify(fileUploadService, times(1)).uploadFileAndSendEmail(captorMultipartFile.capture(), eq(customerName));
        Assertions.assertNull(captorMultipartFile.getValue());
    }

    @Test
    void givenMissingNameRequestThenExpectFail() throws Exception {
        doThrow(new BusinessException("Request property name is missing", HttpStatus.BAD_REQUEST))
                .when(fileUploadService).uploadFileAndSendEmail(any(), any());

        mockMvc.perform(MockMvcRequestBuilders.multipart(UPLOAD_FILE_URL)
                        .file("file", file.getBytes()).characterEncoding(StandardCharsets.UTF_8)
                        .header("authKey", authKey)
                )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isBadRequest());

        verify(fileUploadService, times(1)).uploadFileAndSendEmail(captorMultipartFile.capture(), eq(null));
        Assertions.assertNotNull(captorMultipartFile.getValue());
    }

    private ResponseModel<UploadFileResponse> mockResponseModel() {
        return new ResponseModel<UploadFileResponse>()
                .setDataObject(
                    UploadFileResponse.builder()
                    .isSuccess(true)
                    .build()
                );
    }
}
