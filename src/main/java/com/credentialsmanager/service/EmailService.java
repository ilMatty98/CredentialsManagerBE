package com.credentialsmanager.service;

import com.credentialsmanager.dto.EmailDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {

    @Value("${mail.from}")
    private String emailFrom;

    private final JavaMailSender emailSender;

    public boolean sendEmail(EmailDto emailDto) {
        var from = emailDto.getFrom() == null ? emailFrom : emailDto.getFrom();
        var to = emailDto.getTo();
        var cc = emailDto.getCc();

        if (from == null || !isValidEmailAddress(from)) {
            log.warn(MessageUtils.ERRORE_08.getMessage(FROM.getLabel(), from));
            return false;
        }

        if (to == null || to.isEmpty()) {
            log.warn(MessageUtils.ERRORE_09.getMessage());
            return false;
        }

        var correctTo = cleanEmail(to, TO.getLabel());
        var correctCc = cc != null && !cc.isEmpty() ? cleanEmail(cc, CC.getLabel()) : new String[]{};

        if (correctTo.length == 0) {
            log.warn(MessageUtils.ERRORE_09.getMessage());
            return false;
        }

        try {
            emailSender.send(buildMail(from, correctTo, correctCc, emailDto.getSubject(), emailDto.getText()));
        } catch (Exception e) {
            log.warn(MessageUtils.ERRORE_20.getMessage(from, String.join(",", correctTo), e.getMessage()));
            return false;
        }
        return true;
    }

    private MimeMessagePreparator buildMail(String from, String[] to, String[] cc, String subject, String text) {
        return mimeMessage -> {
            var message = new MimeMessageHelper(mimeMessage, true, "UTF-8");
            message.setFrom(from);
            message.setSubject(subject);
            message.setText(text);
            message.setTo(to);
            if (cc.length > 0) message.setCc(cc);
        };
    }

    private static String[] cleanEmail(String emails, String type) {
        var emailList = Optional.ofNullable(emails).map(s -> s.split(",")).orElse(new String[]{});
        var correctEmails = new ArrayList<String>();
        for (String email : emailList) {
            email = email.trim();
            if (isValidEmailAddress(email)) {
                correctEmails.add(email);
            } else {
                log.warn("Email '{}' not correct {}", type, email);
            }
        }
        return correctEmails.toArray(new String[]{});
    }

    private static boolean isValidEmailAddress(String email) {
        var result = true;
        try {
            var emailAddr = new InternetAddress(email);
            emailAddr.validate();
        } catch (AddressException ex) {
            result = false;
        }
        return result;
    }
}

