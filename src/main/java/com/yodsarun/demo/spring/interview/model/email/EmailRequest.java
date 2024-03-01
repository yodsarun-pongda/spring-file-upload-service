package com.yodsarun.demo.spring.interview.model.email;

import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.Map;

@Data
@Builder
public class EmailRequest {
    private String mailForm;
    private String mailTo;
    private String subject;
    private String template;
    private Map<String, Object> paramsMap;
}
