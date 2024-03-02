package com.yodsarun.demo.spring.interview.config.email;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;

import java.nio.charset.StandardCharsets;
import java.util.Properties;

@Configuration
public class EmailConfig {
    @Value("${email-config.user-name}")
    private String userName;

    @Value("${email-config.app-password}")
    private String appPassword;

    @Bean(name = "mailSender")
    public JavaMailSender javaMailSender() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost("smtp.gmail.com");
        mailSender.setPort(587);
        mailSender.setUsername(userName);
        mailSender.setPassword(appPassword);

        Properties prop = mailSender.getJavaMailProperties();
        prop.setProperty("mail.transport.protocol", "smtp");
        prop.setProperty("mail.smtp.auth", "true");
        prop.setProperty("mail.smtp.starttls.enable", "true");
        prop.setProperty("mail.debug", "true");

        return mailSender;
    }

    @Bean(name = "mimeMessage")
    public MimeMessage mimeMessage(@Qualifier("mailSender") JavaMailSender mailSender) {
        return mailSender.createMimeMessage();
    }

    @Bean(name = "messageHelper")
    public MimeMessageHelper messageHelper(@Qualifier("mimeMessage") MimeMessage mimeMessage) throws MessagingException {
        return new MimeMessageHelper(mimeMessage, true, StandardCharsets.UTF_8.name());
    }
}
