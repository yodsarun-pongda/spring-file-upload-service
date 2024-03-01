package com.yodsarun.demo.spring.interview.service;

import com.yodsarun.demo.spring.interview.exception.BusinessException;
import com.yodsarun.demo.spring.interview.model.email.EmailRequest;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.http.HttpStatus;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.nio.charset.StandardCharsets;
import java.util.Locale;

@Service
@Slf4j
@RequiredArgsConstructor
public class EmailService {
    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;

    public void sentEmail(EmailRequest emailRequest) {
        try {
            // send email
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper message = new MimeMessageHelper(mimeMessage, true, StandardCharsets.UTF_8.name());
            message.setSubject(emailRequest.getSubject());
            message.setTo(emailRequest.getMailTo());
            message.setText(buildEmailContent(emailRequest), true);
            mailSender.send(mimeMessage);
        } catch (Exception e) {
            log.error("Can not sent email to customer: {}", emailRequest.getMailTo(), e);
            throw new BusinessException(ExceptionUtils.getRootCauseMessage(e), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private String buildEmailContent(EmailRequest emailRequest) {
        Context context = new Context(Locale.forLanguageTag("TH"), emailRequest.getParamsMap());
        return templateEngine.process(emailRequest.getTemplate(), context);
    }
}
