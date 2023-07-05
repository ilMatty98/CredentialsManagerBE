package com.credentialsmanager.service;

import com.credentialsmanager.constants.EmailTypeEnum;
import com.credentialsmanager.constants.MessageEnum;
import com.credentialsmanager.dto.EmailTemplateDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Pattern;

import static com.credentialsmanager.constants.EmailTypeEnum.EmailConstants.DEFAULT_LANGUAGE;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    @Value("${mail.from}")
    private String emailFrom;

    private final ObjectMapper objectMapper;

    private final JavaMailSender emailSender;

    /** "\\$\\{[^}]+}" --> ${variableName} **/
    private static final String REGEX = "\\$\\{[^}]+}";

    public void sendEmail(String email, String language, EmailTypeEnum emailTypeEnum, Map<String, String> dynamicLabels) {
        try {
            var labels = objectMapper.readValue(Paths.get(emailTypeEnum.getLabelLocation()).toFile(), EmailTemplateDto.class);
            dynamicLabels.forEach((k, v) -> labels.getTemplate().put(k, Collections.singletonMap(DEFAULT_LANGUAGE, v)));
            var template = FileUtils.readFileToString(Paths.get(emailTypeEnum.getTemplateLocation()).toFile(), StandardCharsets.UTF_8);

            var subject = getValue(labels.getSubject(), language);
            var templateFiller = fillTemplate(labels.getTemplate(), template, language);

            emailSender.send(buildMail(emailFrom, email, subject, templateFiller));
        } catch (Exception e) {
            log.warn(MessageEnum.ERROR_99.getMessage(email), e.getMessage());
        }
    }

    private static MimeMessagePreparator buildMail(String from, String to, String subject, String text) {
        return mimeMessage -> {
            var message = new MimeMessageHelper(mimeMessage, true, StandardCharsets.UTF_8.name());
            message.setFrom(from);
            message.setSubject(subject);
            message.setText(text, true);
            message.setTo(to);
        };
    }

    private static String getValue(Map<String, String> map, String language) {
        try {
            return map.getOrDefault(language, map.getOrDefault(DEFAULT_LANGUAGE, ""));
        } catch (Exception e) {
            return "";
        }
    }

    private static String fillTemplate(Map<String, Map<String, String>> labelsMap, String template, String language) {
        try {
            var substrings = extractSubstrings(template);
            for (var s : substrings) {
                var key = s.substring(2, s.length() - 1);
                template = template.replace(s, getValue(labelsMap.get(key), language));
            }
            return Optional.ofNullable(template).orElse("");
        } catch (Exception e) {
            return template;
        }
    }

    private static List<String> extractSubstrings(String input) {
        try {
            var matcher = Pattern.compile(REGEX).matcher(input);
            var substrings = new ArrayList<String>();
            while (matcher.find()) {
                substrings.add(matcher.group());
            }
            return substrings;
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

}

