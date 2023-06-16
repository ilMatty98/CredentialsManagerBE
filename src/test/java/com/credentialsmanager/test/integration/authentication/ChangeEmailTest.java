package com.credentialsmanager.test.integration.authentication;

import com.credentialsmanager.constants.UserStateEnum;
import com.credentialsmanager.dto.request.ChangeEmailDto;
import com.credentialsmanager.test.ApiTest;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ChangeEmailTest extends ApiTest {

    @Test
    void testWithoutToken() throws Exception {
        var mockHttpServletRequestBuilder = patch(CHANGE_EMAIL_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectToJsonString(new ChangeEmailDto()));

        mockMvc.perform(mockHttpServletRequestBuilder)
                .andExpect(status().isBadRequest());
    }

    @Test
    void testChangeEmailDtoEmpty() throws Exception {
        signUp(EMAIL, PASSWORD);
        confirmEmail(EMAIL);
        var mockHttpServletRequestBuilder = patch(CHANGE_EMAIL_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .header(AUTH_HEADER_NAME, AUTH_HEADER_PREFIX + getTokenFromLogIn(EMAIL, PASSWORD))
                .content(objectToJsonString(new ChangeEmailDto()));

        mockMvc.perform(mockHttpServletRequestBuilder)
                .andExpect(status().isBadRequest());
    }

    @Test
    void testNewEmailNotValid() throws Exception {
        signUp(EMAIL, PASSWORD);
        confirmEmail(EMAIL);

        var changeEmailDto = new ChangeEmailDto();
        changeEmailDto.setEmail("aaaa");

        var mockHttpServletRequestBuilder = patch(CHANGE_EMAIL_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .header(AUTH_HEADER_NAME, AUTH_HEADER_PREFIX + getTokenFromLogIn(EMAIL, PASSWORD))
                .content(objectToJsonString(changeEmailDto));

        mockMvc.perform(mockHttpServletRequestBuilder)
                .andExpect(status().isBadRequest());
    }

    @Test
    void testUserNotFoundForEmail() throws Exception {
        var user = signUp(EMAIL, PASSWORD);
        user = confirmEmail(EMAIL);
        var changeEmailDto = new ChangeEmailDto();
        changeEmailDto.setEmail("test2@test.com");

        var token = getTokenFromLogIn(EMAIL, PASSWORD);

        var mockHttpServletRequestBuilder = patch(CHANGE_EMAIL_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .header(AUTH_HEADER_NAME, AUTH_HEADER_PREFIX + token)
                .content(objectToJsonString(changeEmailDto));

        user.setEmail(EMAIL + ".");
        userRepository.save(user);

        mockMvc.perform(mockHttpServletRequestBuilder)
                .andExpect(status().isNotFound());
    }

    @Test
    void testUserNotFoundForState() throws Exception {
        var user = signUp(EMAIL, PASSWORD);
        user = confirmEmail(EMAIL);
        var changeEmailDto = new ChangeEmailDto();
        changeEmailDto.setEmail("test2@test.com");

        var token = getTokenFromLogIn(EMAIL, PASSWORD);

        var mockHttpServletRequestBuilder = patch(CHANGE_EMAIL_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .header(AUTH_HEADER_NAME, AUTH_HEADER_PREFIX + token)
                .content(objectToJsonString(changeEmailDto));

        user.setState(UserStateEnum.UNVERIFIED);
        userRepository.save(user);

        mockMvc.perform(mockHttpServletRequestBuilder)
                .andExpect(status().isNotFound());
    }

    @Test
    void testChangeEmail() throws Exception {
        var newEmail = "test2@test.com";
        signUp(EMAIL, PASSWORD);
        final var user = confirmEmail(EMAIL);
        var changeEmailDto = new ChangeEmailDto();
        changeEmailDto.setEmail(newEmail);

        var mockHttpServletRequestBuilder = patch(CHANGE_EMAIL_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .header(AUTH_HEADER_NAME, AUTH_HEADER_PREFIX + getTokenFromLogIn(EMAIL, PASSWORD))
                .content(objectToJsonString(changeEmailDto));

        mockMvc.perform(mockHttpServletRequestBuilder)
                .andExpect(status().isOk());

        userRepository.findByEmail(newEmail).ifPresentOrElse(u -> {
            assertEquals(user.getId(), u.getId());
            assertEquals(newEmail, u.getEmail());
            assertEquals(user.getSalt(), u.getSalt());
            assertEquals(user.getHash(), u.getHash());
            assertEquals(user.getProtectedSymmetricKey(), u.getProtectedSymmetricKey());
            assertEquals(user.getInitializationVector(), u.getInitializationVector());
            assertEquals(getLocalDataTime(user.getTimestampCreation()), getLocalDataTime(u.getTimestampCreation()));
            assertTrue(user.getTimestampLastAccess().before(u.getTimestampLastAccess()));
            assertEquals(getLocalDataTime(user.getTimestampPassword()), getLocalDataTime(u.getTimestampPassword()));
            assertEquals(user.getLanguage(), u.getLanguage());
            assertEquals(user.getHint(), u.getHint());
            assertEquals(user.getPropic(), u.getPropic());
            assertEquals(UserStateEnum.VERIFIED, u.getState());
            assertNull(u.getVerificationCode());
        }, Assert::fail);

        //Check email
        var receivedMessages = greenMail.getReceivedMessages();
        assertTrue(greenMail.waitForIncomingEmail(5000, 1));
        assertEquals(3, receivedMessages.length);

        var email = receivedMessages[2];
        assertEquals(1, email.getAllRecipients().length);
        assertEquals(emailFrom, email.getFrom()[0].toString());
        assertEquals(newEmail, email.getAllRecipients()[0].toString());
        assertEquals("Email changed!", email.getSubject());
    }
}
