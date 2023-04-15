package com.credentialsmanager.service;

import com.credentialsmanager.constants.EmailType;
import com.credentialsmanager.constants.MessageUtils;
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

import static com.credentialsmanager.constants.EmailType.EmailConstants.DEFAULT_LANGUAGE;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    @Value("${mail.from}")
    private String emailFrom;

    private final ObjectMapper objectMapper;

    private final JavaMailSender emailSender;


    public boolean sendEmail(String email, String language, EmailType emailType) {
        try {
            var labelsMap = objectMapper.readValue(Paths.get(emailType.getLabelLocation()).toFile(), Map.class);
            var template = FileUtils.readFileToString(Paths.get(emailType.getTemplateLocation()).toFile(), StandardCharsets.UTF_8);

            var subject = getValue((LinkedHashMap<?, ?>) labelsMap.get(EmailType.EmailConstants.KEY_SUBJECT), language);
            var templateFiller = fillTemplate((LinkedHashMap<?, ?>) labelsMap.get(EmailType.EmailConstants.KEY_TEMPLATE), template, language);

            emailSender.send(buildMail(emailFrom, email, subject, templateFiller));
            return true;
        } catch (Exception e) {
            log.warn(MessageUtils.ERROR_04.getMessage(email), e.getMessage());
            return false;
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

    private static String getValue(Map<?, ?> map, String language) {
        try {
            if (map.containsKey(language)) {
                return map.get(language).toString();
            } else if (map.containsKey(DEFAULT_LANGUAGE)) {
                return map.get(DEFAULT_LANGUAGE).toString();
            }
            return "";
        } catch (Exception e) {
            return "";
        }
    }

    private static String fillTemplate(Map<?, ?> labelsMap, String template, String language) {
        try {
            var substrings = extractSubstrings(template, "\\$\\{[^}]+\\}");
            for (var substring : substrings) {
                var key = substring.substring(2, substring.length() - 1);
                template = template.replace(substring, getValue((LinkedHashMap<?, ?>) labelsMap.get(key), language));
            }
            return Optional.ofNullable(template).orElse("");
        } catch (Exception e) {
            return "";
        }
    }

    public static List<String> extractSubstrings(String input, String regex) {
        try {
            var matcher = Pattern.compile(regex).matcher(input);
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

