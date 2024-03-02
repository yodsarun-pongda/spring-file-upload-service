package com.yodsarun.demo.spring.interview.service;

import com.yodsarun.demo.spring.interview.exception.BusinessException;
import com.yodsarun.demo.spring.interview.model.email.EmailRequest;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.test.util.ReflectionTestUtils;
import org.thymeleaf.TemplateEngine;

import java.util.HashMap;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@Slf4j
@ExtendWith(MockitoExtension.class)
public class EmailServiceTest {
    @InjectMocks
    EmailService emailService;

    @Mock
    JavaMailSender mailSender;

    @Mock
    TemplateEngine templateEngine;

    @Mock
    MimeMessage mimeMessage;

    @Mock
    MimeMessageHelper messageHelper;

    @Captor
    ArgumentCaptor<MimeMessage> emailCapture;

//    @BeforeEach
//    void setUp() {
//        doNothing().when(mailSender).send(any(MimeMessage.class));
//    }

    @Test
    @DisplayName("Send email case with all required fields expect success")
    void whenSendEmailThenExpectSuccess() {
        doNothing().when(mailSender).send(any(MimeMessage.class));

        var request = EmailRequest.builder()
                .subject("subject")
                .mailTo("mailTo")
                .template("thymeleaf-template.html")
                .paramsMap(new HashMap<>())
                .build();
        emailService.sentEmail(request);

        verify(mailSender, times(1)).send(any(MimeMessage.class));
    }

    @Test
    @DisplayName("Given can not create bean sender Then expect fail")
    void givenCanNotConfigEmailSenderWhenSendEmailThenExpectFail() {
        ReflectionTestUtils.setField(emailService, "mailSender", null);

        var request = EmailRequest.builder()
                .subject("subject")
                .mailTo("mailTo")
                .template("thymeleaf-template.html")
                .paramsMap(new HashMap<>())
                .build();
        var actualException =
                Assertions.assertThrows(BusinessException.class, () -> emailService.sentEmail(request));

        Assertions.assertTrue(actualException.getMessage().contains("NullPointerException"));
        Assertions.assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, actualException.getHttpStatus());
        verify(mailSender, never()).send(any(MimeMessage.class));
    }

    @Test
    @DisplayName("Given emailRequest is empty Then expect fail")
    void givenEmailRequestIsEmptyWhenSendEmailThenExpectFail() {
        ReflectionTestUtils.setField(emailService, "mailSender", null);

        var actualException =
                Assertions.assertThrows(BusinessException.class, () -> emailService.sentEmail(null));

        Assertions.assertTrue(actualException.getMessage().contains("NullPointerException"));
        Assertions.assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, actualException.getHttpStatus());
        verify(mailSender, never()).send(any(MimeMessage.class));
    }
}
