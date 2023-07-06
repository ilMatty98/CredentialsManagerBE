package com.credentialsmanager.test.integration.authentication;

import com.credentialsmanager.constants.MessageEnum;
import com.credentialsmanager.constants.UserStateEnum;
import com.credentialsmanager.dto.request.ConfirmChangeEmailDto;
import com.credentialsmanager.entity.User;
import com.credentialsmanager.test.ApiTest;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import java.sql.Timestamp;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ConfirmChangeEmailTest extends ApiTest {

    private static final String NEW_EMAIL = "new" + EMAIL;

    @Test
    void testWithoutToken() throws Exception {
        var mockHttpServletRequestBuilder = put(CONFIRM_CHANGE_EMAIL_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectToJsonString(new ConfirmChangeEmailDto()));

        mockMvc.perform(mockHttpServletRequestBuilder)
                .andExpect(status().isBadRequest());
    }

    @Test
    void testConfirmChangeEmailDtoEmpty() throws Exception {
        signUp(EMAIL, PASSWORD);
        confirmEmail(EMAIL);
        var mockHttpServletRequestBuilder = put(CONFIRM_CHANGE_EMAIL_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .header(AUTH_HEADER_NAME, AUTH_HEADER_PREFIX + getTokenFromLogIn(EMAIL, PASSWORD))
                .content(objectToJsonString(new ConfirmChangeEmailDto()));

        mockMvc.perform(mockHttpServletRequestBuilder)
                .andExpect(status().isBadRequest());
    }

    @Test
    void testVerificationCodeNull() throws Exception {
        signUp(EMAIL, PASSWORD);
        confirmEmail(EMAIL);

        var changeEmailDto = new ConfirmChangeEmailDto();
        changeEmailDto.setEmail(EMAIL);
        changeEmailDto.setMasterPasswordHash(PASSWORD);

        var mockHttpServletRequestBuilder = put(CONFIRM_CHANGE_EMAIL_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .header(AUTH_HEADER_NAME, AUTH_HEADER_PREFIX + getTokenFromLogIn(EMAIL, PASSWORD))
                .content(objectToJsonString(changeEmailDto));

        mockMvc.perform(mockHttpServletRequestBuilder)
                .andExpect(status().isBadRequest());
    }

    @Test
    void testProtectedSymmetricKeyNull() throws Exception {
        signUp(EMAIL, PASSWORD);
        confirmEmail(EMAIL);

        var changeEmailDto = new ConfirmChangeEmailDto();
        changeEmailDto.setEmail(EMAIL);
        changeEmailDto.setMasterPasswordHash(PASSWORD);
        changeEmailDto.setInitializationVector("iv");

        var mockHttpServletRequestBuilder = put(CONFIRM_CHANGE_EMAIL_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .header(AUTH_HEADER_NAME, AUTH_HEADER_PREFIX + getTokenFromLogIn(EMAIL, PASSWORD))
                .content(objectToJsonString(changeEmailDto));

        mockMvc.perform(mockHttpServletRequestBuilder)
                .andExpect(status().isBadRequest());
    }

    @Test
    void testInitializationVectorNull() throws Exception {
        signUp(EMAIL, PASSWORD);
        confirmEmail(EMAIL);

        var changeEmailDto = new ConfirmChangeEmailDto();
        changeEmailDto.setEmail(EMAIL);
        changeEmailDto.setMasterPasswordHash(PASSWORD);
        changeEmailDto.setProtectedSymmetricKey("pr");

        var mockHttpServletRequestBuilder = put(CONFIRM_CHANGE_EMAIL_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .header(AUTH_HEADER_NAME, AUTH_HEADER_PREFIX + getTokenFromLogIn(EMAIL, PASSWORD))
                .content(objectToJsonString(changeEmailDto));

        mockMvc.perform(mockHttpServletRequestBuilder)
                .andExpect(status().isBadRequest());
    }

    @Test
    void testNewEmailAlreadyExist() throws Exception {
        signUp(EMAIL, PASSWORD);
        confirmEmail(EMAIL);
        changeEmail(EMAIL, PASSWORD, NEW_EMAIL);

        signUp(EMAIL + "a", PASSWORD);

        var changeEmailDto = new ConfirmChangeEmailDto();
        changeEmailDto.setEmail(EMAIL + "a");
        changeEmailDto.setMasterPasswordHash(PASSWORD);
        changeEmailDto.setVerificationCode("asd");
        changeEmailDto.setProtectedSymmetricKey("new protectedSymmetricKey");
        changeEmailDto.setInitializationVector("new initializationVector");

        var mockHttpServletRequestBuilder = put(CONFIRM_CHANGE_EMAIL_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .header(AUTH_HEADER_NAME, AUTH_HEADER_PREFIX + getTokenFromLogIn(EMAIL, PASSWORD))
                .content(objectToJsonString(changeEmailDto));

        mockMvc.perform(mockHttpServletRequestBuilder)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath(MESSAGE).value(MessageEnum.ERROR_01.getLabel()));
    }

    @Test
    void testUserNotFound() throws Exception {
        signUp(EMAIL, PASSWORD);
        confirmEmail(EMAIL);
        changeEmail(EMAIL, PASSWORD, NEW_EMAIL);

        var changeEmailDto = new ConfirmChangeEmailDto();
        changeEmailDto.setEmail(NEW_EMAIL + "a");
        changeEmailDto.setMasterPasswordHash(PASSWORD);
        changeEmailDto.setVerificationCode("asd");
        changeEmailDto.setProtectedSymmetricKey("new protectedSymmetricKey");
        changeEmailDto.setInitializationVector("new initializationVector");

        var mockHttpServletRequestBuilder = put(CONFIRM_CHANGE_EMAIL_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .header(AUTH_HEADER_NAME, AUTH_HEADER_PREFIX + getTokenFromLogIn(EMAIL, PASSWORD))
                .content(objectToJsonString(changeEmailDto));

        mockMvc.perform(mockHttpServletRequestBuilder)
                .andExpect(status().isNotFound())
                .andExpect(jsonPath(MESSAGE).value(MessageEnum.ERROR_03.getLabel()));
    }

    @Test
    void testPasswordDoesNotCoincide() throws Exception {
        signUp(EMAIL, PASSWORD);
        confirmEmail(EMAIL);
        changeEmail(EMAIL, PASSWORD, NEW_EMAIL);

        var changeEmailDto = new ConfirmChangeEmailDto();
        changeEmailDto.setEmail(NEW_EMAIL);
        changeEmailDto.setMasterPasswordHash(PASSWORD + "a");
        changeEmailDto.setVerificationCode("asd");
        changeEmailDto.setProtectedSymmetricKey("new protectedSymmetricKey");
        changeEmailDto.setInitializationVector("new initializationVector");

        var mockHttpServletRequestBuilder = put(CONFIRM_CHANGE_EMAIL_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .header(AUTH_HEADER_NAME, AUTH_HEADER_PREFIX + getTokenFromLogIn(EMAIL, PASSWORD))
                .content(objectToJsonString(changeEmailDto));

        mockMvc.perform(mockHttpServletRequestBuilder)
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath(MESSAGE).value(MessageEnum.ERROR_02.getLabel()));
    }

    @Test
    void testTimeOut() throws Exception {
        signUp(EMAIL, PASSWORD);
        confirmEmail(EMAIL);
        var user = changeEmail(EMAIL, PASSWORD, NEW_EMAIL);
        user.setTimestampEmail(Timestamp.valueOf(user.getTimestampEmail().toLocalDateTime().minusMinutes(5)));
        userRepository.save(user);

        var changeEmailDto = new ConfirmChangeEmailDto();
        changeEmailDto.setEmail(NEW_EMAIL);
        changeEmailDto.setMasterPasswordHash(PASSWORD);
        changeEmailDto.setVerificationCode("asd");
        changeEmailDto.setProtectedSymmetricKey("new protectedSymmetricKey");
        changeEmailDto.setInitializationVector("new initializationVector");

        var mockHttpServletRequestBuilder = put(CONFIRM_CHANGE_EMAIL_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .header(AUTH_HEADER_NAME, AUTH_HEADER_PREFIX + getTokenFromLogIn(EMAIL, PASSWORD))
                .content(objectToJsonString(changeEmailDto));

        mockMvc.perform(mockHttpServletRequestBuilder)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath(MESSAGE).value(MessageEnum.ERROR_06.getLabel()));

        checkUser(user, EMAIL, null, null, null);
    }

    @Test
    void testlimitAttemps() throws Exception {
        signUp(EMAIL, PASSWORD);
        confirmEmail(EMAIL);
        var user = changeEmail(EMAIL, PASSWORD, NEW_EMAIL);
        user.setAttempt(10);
        userRepository.save(user);

        var changeEmailDto = new ConfirmChangeEmailDto();
        changeEmailDto.setEmail(NEW_EMAIL);
        changeEmailDto.setMasterPasswordHash(PASSWORD);
        changeEmailDto.setVerificationCode("asd");
        changeEmailDto.setProtectedSymmetricKey("new protectedSymmetricKey");
        changeEmailDto.setInitializationVector("new initializationVector");

        var mockHttpServletRequestBuilder = put(CONFIRM_CHANGE_EMAIL_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .header(AUTH_HEADER_NAME, AUTH_HEADER_PREFIX + getTokenFromLogIn(EMAIL, PASSWORD))
                .content(objectToJsonString(changeEmailDto));

        mockMvc.perform(mockHttpServletRequestBuilder)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath(MESSAGE).value(MessageEnum.ERROR_05.getLabel()));

        checkUser(user, EMAIL, null, null, null);
    }

    @Test
    void testIncorrectVerificationCode() throws Exception {
        signUp(EMAIL, PASSWORD);
        confirmEmail(EMAIL);
        var user = changeEmail(EMAIL, PASSWORD, NEW_EMAIL);

        var changeEmailDto = new ConfirmChangeEmailDto();
        changeEmailDto.setEmail(NEW_EMAIL);
        changeEmailDto.setMasterPasswordHash(PASSWORD);
        changeEmailDto.setVerificationCode("asd");
        changeEmailDto.setProtectedSymmetricKey("new protectedSymmetricKey");
        changeEmailDto.setInitializationVector("new initializationVector");

        var mockHttpServletRequestBuilder = put(CONFIRM_CHANGE_EMAIL_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .header(AUTH_HEADER_NAME, AUTH_HEADER_PREFIX + getTokenFromLogIn(EMAIL, PASSWORD))
                .content(objectToJsonString(changeEmailDto));

        mockMvc.perform(mockHttpServletRequestBuilder)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath(MESSAGE).value(MessageEnum.ERROR_07.getLabel()));

        checkUser(user, EMAIL, user.getVerificationCode(), NEW_EMAIL, 1);
    }

    @Test
    void testConfirmChangeEmail() throws Exception {
        signUp(EMAIL, PASSWORD);
        confirmEmail(EMAIL);
        var user = changeEmail(EMAIL, PASSWORD, NEW_EMAIL);

        var changeEmailDto = new ConfirmChangeEmailDto();
        changeEmailDto.setEmail(NEW_EMAIL);
        changeEmailDto.setMasterPasswordHash(PASSWORD);
        changeEmailDto.setVerificationCode(user.getVerificationCode());
        changeEmailDto.setProtectedSymmetricKey("new protectedSymmetricKey");
        changeEmailDto.setInitializationVector("new initializationVector");

        var mockHttpServletRequestBuilder = put(CONFIRM_CHANGE_EMAIL_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .header(AUTH_HEADER_NAME, AUTH_HEADER_PREFIX + getTokenFromLogIn(EMAIL, PASSWORD))
                .content(objectToJsonString(changeEmailDto));

        mockMvc.perform(mockHttpServletRequestBuilder)
                .andExpect(status().isOk());

        user.setProtectedSymmetricKey(authenticationMapper.base64EncodingString("new protectedSymmetricKey"));
        user.setInitializationVector(authenticationMapper.base64EncodingString("new initializationVector"));
        checkUser(user, NEW_EMAIL, null, null, null);

        assertNotNull(getTokenFromLogIn(NEW_EMAIL, PASSWORD));

        //Check email
        var receivedMessages = greenMail.getReceivedMessages();
        assertTrue(greenMail.waitForIncomingEmail(5000, 1));
        assertEquals(7, receivedMessages.length);

        var emailChanged = receivedMessages[5];
        assertEquals(1, emailChanged.getAllRecipients().length);
        assertEquals(emailFrom, emailChanged.getFrom()[0].toString());
        assertEquals(NEW_EMAIL, emailChanged.getAllRecipients()[0].toString());
        assertEquals("Email changed!", emailChanged.getSubject());
    }

    private void checkUser(User user, String email, String verificationCode, String newEmail, Integer attempt) {
        userRepository.findByEmail(email).ifPresentOrElse(u -> {
            assertEquals(user.getId(), u.getId());
            assertEquals(email, u.getEmail());
            assertEquals(user.getSalt(), u.getSalt());
            assertEquals(user.getHash(), u.getHash());
            assertEquals(user.getProtectedSymmetricKey(), u.getProtectedSymmetricKey());
            assertEquals(user.getInitializationVector(), u.getInitializationVector());
            assertEquals(getLocalDataTime(user.getTimestampCreation()), getLocalDataTime(u.getTimestampCreation()));
            assertTrue(user.getTimestampLastAccess().before(u.getTimestampLastAccess()));
            assertEquals(getLocalDataTime(user.getTimestampPassword()), getLocalDataTime(u.getTimestampPassword()));
            assertTrue(u.getTimestampEmail().before(Timestamp.from(Instant.now())));
            assertEquals(user.getLanguage(), u.getLanguage());
            assertEquals(user.getHint(), u.getHint());
            assertEquals(user.getPropic(), u.getPropic());
            assertEquals(UserStateEnum.VERIFIED, u.getState());
            assertEquals(verificationCode, u.getVerificationCode());
            assertEquals(newEmail, u.getNewEmail());
            assertEquals(attempt, u.getAttempt());
        }, Assert::fail);
    }


}
