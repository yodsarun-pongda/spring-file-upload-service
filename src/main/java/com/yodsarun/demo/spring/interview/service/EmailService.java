package com.yodsarun.demo.spring.interview.service;

import com.yodsarun.demo.spring.interview.exception.BusinessException;
import com.yodsarun.demo.spring.interview.model.email.EmailRequest;
import jakarta.mail.internet.MimeMessage;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.Locale;

@Service
@Slf4j
@RequiredArgsConstructor
public class EmailService {
    @Qualifier("mailSender") @NonNull private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;
    @Qualifier("mimeMessage") @NonNull private final MimeMessage mimeMessage;
    @Qualifier("messageHelper") @NonNull private final MimeMessageHelper messageHelper;

    public void sentEmail(EmailRequest emailRequest) {
        try {
            // send email
            messageHelper.setSubject(emailRequest.getSubject());
            messageHelper.setTo(emailRequest.getMailTo());
            messageHelper.setText(buildEmailContent(emailRequest), true);
            mailSender.send(mimeMessage);
        } catch (Exception e) {
            log.error("Can not sent email to customer", e);
            throw new BusinessException(ExceptionUtils.getRootCauseMessage(e), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private String buildEmailContent(EmailRequest emailRequest) {
        Context context = new Context(Locale.forLanguageTag("TH"), emailRequest.getParamsMap());
        return templateEngine.process(emailRequest.getTemplate(), context);
    }
}
