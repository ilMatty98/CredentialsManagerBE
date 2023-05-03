package com.credentialsmanager.test.service;

import com.credentialsmanager.constants.EmailTypeEnum;
import com.credentialsmanager.test.ApiTest;
import com.icegreen.greenmail.util.GreenMailUtil;
import jakarta.mail.MessagingException;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class EmailServiceTest extends ApiTest {

    private static final String EMAIL_TO = "test@test.com";
    private static final String EN = "EN";
    private static final String IT = "IT";

    @Test
    void testEmailIT() throws MessagingException {
        verifyLanguage(IT, "Nuovo accesso su Credential Manager!", "Data:");
    }

    @Test
    void testEmailEN() throws MessagingException {
        verifyLanguage(EN, "New access on Credential Manager!", "Date:");
    }

    @Test
    void testEmailDifferentLanguage() throws MessagingException {
        verifyLanguage("HR", "New access on Credential Manager!", "Date:");
    }

    @Test
    void testEmailLogIn() throws MessagingException {
        var expectedSubject = "New access on Credential Manager!";
        var label = List.of("Date", "IP Address", "Device Type", "Credentials Manager");
        var dynamicLabels = new HashMap<String, String>();
        dynamicLabels.put("date_value", LocalDateTime.now().toString());
        dynamicLabels.put("ipAddress_value", "255.255.255.255");
        dynamicLabels.put("device_value", "Chrome");

        verifyEmail(EmailTypeEnum.LOG_IN, expectedSubject, label, dynamicLabels);
    }

    @Test
    void testEmailSignUp() throws MessagingException {
        var expectedSubject = "Welcome to Credentials Manager!";
        var dynamicLabels = new HashMap<String, String>();
        var label = List.of("Welcome to Credentials Manager!", "Click on this link to confirm the account", "Credentials Manager");
        dynamicLabels.put("href", generateRandomString(20));

        verifyEmail(EmailTypeEnum.SING_UP, expectedSubject, label, dynamicLabels);
    }

    @Test
    void testEmailChangePsw() throws MessagingException {
        var expectedSubject = "Password changed!";
        var dynamicLabels = new HashMap<String, String>();
        var label = List.of("Password changed!", "Password has been changed!", "Credentials Manager");
        dynamicLabels.put("href", generateRandomString(20));

        verifyEmail(EmailTypeEnum.CHANGE_PSW, expectedSubject, label, dynamicLabels);
    }

    private void verifyLanguage(String language, String expectedSubject, String expectedContainsBody) throws MessagingException {
        emailService.sendEmail(EMAIL_TO, language, EmailTypeEnum.LOG_IN, new HashMap<>());

        var receivedMessages = greenMail.getReceivedMessages();
        assertTrue(greenMail.waitForIncomingEmail(5000, 1));
        assertEquals(1, receivedMessages.length);

        var email = receivedMessages[0];
        assertEquals(1, email.getAllRecipients().length);
        assertEquals(emailFrom, email.getFrom()[0].toString());
        assertEquals(EMAIL_TO, email.getAllRecipients()[0].toString());
        assertEquals(expectedSubject, email.getSubject());
        assertTrue(GreenMailUtil.getBody(email).contains(expectedContainsBody));
    }

    private void verifyEmail(EmailTypeEnum emailType, String expectedSubject, List<String> label,
                             Map<String, String> dynamicLabels) throws MessagingException {
        emailService.sendEmail(EMAIL_TO, EmailServiceTest.EN, emailType, dynamicLabels);

        var receivedMessages = greenMail.getReceivedMessages();
        assertTrue(greenMail.waitForIncomingEmail(5000, 1));
        assertEquals(1, receivedMessages.length);

        var email = receivedMessages[0];
        assertEquals(1, email.getAllRecipients().length);
        assertEquals(emailFrom, email.getFrom()[0].toString());
        assertEquals(EMAIL_TO, email.getAllRecipients()[0].toString());
        assertEquals(expectedSubject, email.getSubject());

        var body = GreenMailUtil.getBody(email);

        var elements = Stream.concat(dynamicLabels.values().stream(), label.stream()).toList();
        assertTrue(elements.stream().allMatch(body::contains));
    }

}
