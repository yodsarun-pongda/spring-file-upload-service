package com.yodsarun.demo.spring.interview.config.app;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@EnableConfigurationProperties
@ConfigurationProperties(prefix = "app-config.upload-file")
public class UploadFileConfiguration {
    private String mailSubject;
    private String mailTemplate;
    private String savePath;
    private String allowExtension;
}
