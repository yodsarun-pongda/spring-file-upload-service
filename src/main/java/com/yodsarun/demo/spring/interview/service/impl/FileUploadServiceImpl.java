package com.yodsarun.demo.spring.interview.service.impl;

import com.yodsarun.demo.spring.interview.config.app.UploadFileConfiguration;
import com.yodsarun.demo.spring.interview.entity.CustomerEntity;
import com.yodsarun.demo.spring.interview.exception.BusinessException;
import com.yodsarun.demo.spring.interview.model.base.ResponseModel;
import com.yodsarun.demo.spring.interview.model.email.EmailRequest;
import com.yodsarun.demo.spring.interview.model.files.UploadFileResponse;
import com.yodsarun.demo.spring.interview.repository.CustomerRepository;
import com.yodsarun.demo.spring.interview.service.CloudStorageService;
import com.yodsarun.demo.spring.interview.service.EmailService;
import com.yodsarun.demo.spring.interview.service.FileUploadService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class FileUploadServiceImpl implements FileUploadService {
    private final UploadFileConfiguration uploadFileConfiguration;
    private final EmailService emailService;
    private final CloudStorageService cloudStorageService;
    private final CustomerRepository customerRepository;

    @Override
    public ResponseModel<UploadFileResponse> uploadFileAndSendEmail(MultipartFile multipartFile,
                                                                    String customerName) {
        File file = null;
        try {
            // validate request
            validateFileRequest(multipartFile, customerName);

            var fileName = multipartFile.getOriginalFilename();
            var customerEntity = customerRepository.findCustomerEntityByName(customerName);
            if (ObjectUtils.isEmpty(customerEntity)) {
                log.error("customer was not found");
                throw new BusinessException("Customer not found", HttpStatus.NOT_FOUND);
            }

            // Generate file to local storage
            file = convertMultiPartToFile(multipartFile);

            // Upload file to cloud server
            var filePath = file.getAbsolutePath();
            var isSuccess = cloudStorageService.uploadFileToStorage(filePath);

            // Send customer notification
            sentEmail(customerEntity, fileName, isSuccess);

            // Response
            var response = UploadFileResponse.builder()
                    .isSuccess(true)
                    .build();
            return new ResponseModel<UploadFileResponse>("success").setDataObject(response);
        } catch (BusinessException e) {
            log.error("Error occurred when upload process upload file to server", e);
            throw e;
        } catch (Exception e) {
            log.error("Error occurred when upload process upload file to server", e);
            throw new BusinessException(ExceptionUtils.getMessage(e), HttpStatus.INTERNAL_SERVER_ERROR, e);
        } finally {
            if (file != null) {
                // Remove converted file on local storage
                file.deleteOnExit();
            }
        }
    }

    private void validateFileRequest(MultipartFile multipartFile,
                                     String name) {
        if (ObjectUtils.isEmpty(multipartFile) || multipartFile.isEmpty()) {
            throw new BusinessException("Request file is missing", HttpStatus.BAD_REQUEST);
        }

        if (!Arrays.asList(uploadFileConfiguration.getAllowExtension().split(",")).contains(FilenameUtils.getExtension(multipartFile.getOriginalFilename()))) {
            throw new BusinessException("Request File extension is not allowed", HttpStatus.BAD_REQUEST);
        }

        if (StringUtils.isEmpty(name)) {
            throw new BusinessException("Request property name is missing", HttpStatus.BAD_REQUEST);
        }
    }

    private File convertMultiPartToFile(MultipartFile file) throws IOException {
        File convertFile = new File( uploadFileConfiguration.getSavePath().concat("/").concat(Objects.requireNonNull(file.getOriginalFilename())));
        FileOutputStream fos = new FileOutputStream( convertFile );
        fos.write( file.getBytes() );
        fos.close();
        return convertFile;
    }

    private void sentEmail(CustomerEntity customerEntity,
                           String fileName,
                           boolean isSuccess) {
        try {
            var params = new HashMap<String, Object>();
            params.put("name", customerEntity.getName());
            params.put("file_name", fileName);
            params.put("is_success", isSuccess);

            var emailRequest = EmailRequest.builder()
                    .subject(uploadFileConfiguration.getMailSubject())
                    .mailTo(customerEntity.getEmail())
                    .paramsMap(params)
                    .template(uploadFileConfiguration.getMailTemplate())
                    .build();

            emailService.sentEmail(emailRequest);
        } catch (Exception e) {
            log.error("Can't send email to customer", e);
        }
    }
}
