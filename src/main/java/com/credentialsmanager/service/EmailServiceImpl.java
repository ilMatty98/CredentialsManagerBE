package com.credentialsmanager.service;

import com.credentialsmanager.dto.EmailDto;
import com.credentialsmanager.utils.MessageUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    @Value("${mail.from}")
    private String emailFrom;

    private final JavaMailSender emailSender;

    public boolean sendEmail(EmailDto emailDto) {
        try {
            emailSender.send(buildMail(emailFrom, emailDto.to(), emailDto.subject(), emailDto.text()));
            return true;
        } catch (Exception e) {
            log.warn(MessageUtils.ERROR_04.getMessage(emailDto.to()), e.getMessage());
            return false;
        }
    }

    private MimeMessagePreparator buildMail(String from, String to, String subject, String text) {
        return mimeMessage -> {
            var message = new MimeMessageHelper(mimeMessage, true, "UTF-8");
            message.setFrom(from);
            message.setSubject(subject);
            message.setText(text, true);
            message.setTo(to);
        };
    }
}

