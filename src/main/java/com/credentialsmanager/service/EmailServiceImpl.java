package com.credentialsmanager.service;

import com.credentialsmanager.constants.MessageUtils;
import com.credentialsmanager.dto.EmailDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.stereotype.Service;

import java.nio.file.Paths;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    @Value("${mail.from}")
    private String emailFrom;

    private final ObjectMapper objectMapper;

    private final JavaMailSender emailSender;

    private static final String BASE_PATH = "src/main/resources/email/";
    private static final String PATH_BODY = BASE_PATH + "label/body.json";
    private static final String PATH_FOOTER = BASE_PATH + "label/footer.json";
    private static final String PATH_SUBJECT = BASE_PATH + "label/subject.json";
    private static final String TEMPLATE_LOCATION = BASE_PATH + "template/";

    public boolean sendEmail(EmailDto emailDto) {
        try {
            var body = objectMapper.readValue(Paths.get(PATH_BODY).toFile(), Map.class);
            var footer = objectMapper.readValue(Paths.get(PATH_FOOTER).toFile(), Map.class);
            var subject = objectMapper.readValue(Paths.get(PATH_SUBJECT).toFile(), Map.class);
            var template = objectMapper.readValue(Paths.get(TEMPLATE_LOCATION + emailDto.emailType().getFileName()).toFile(), String.class);

            emailSender.send(buildMail(emailFrom, "test@test.test", "emailDto.subject()", "emailDto.text()"));
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

