package com.credentialsmanager.test.integration.authentication;

import com.credentialsmanager.constants.UserStateEnum;
import com.credentialsmanager.test.CredentialsManagerTests;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ConfirmEmailTest extends CredentialsManagerTests {

    @Test
    void testEmailNotFound() throws Exception {
        signUp(ConfirmEmailTest.EMAIL, PASSWORD);
        var mockHttpServletRequestBuilder = patch(CONFIRM_EMAIL_URL, EMAIL + ".", CODE)
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(mockHttpServletRequestBuilder)
                .andExpect(status().isNotFound());
    }

    @Test
    void testCodeNotFound() throws Exception {
        signUp(ConfirmEmailTest.EMAIL, PASSWORD);
        var mockHttpServletRequestBuilder = patch(CONFIRM_EMAIL_URL, EMAIL, CODE + ".")
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(mockHttpServletRequestBuilder)
                .andExpect(status().isNotFound());
    }

    @Test
    void testConfirmEmail() throws Exception {
        var user = signUp(ConfirmEmailTest.EMAIL, PASSWORD);
        var mockHttpServletRequestBuilder = patch(CONFIRM_EMAIL_URL, EMAIL, user.getVerificationCode())
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(mockHttpServletRequestBuilder)
                .andExpect(status().isOk());

        userRepository.findByEmail(EMAIL).ifPresentOrElse(u -> {
            assertEquals(user.getId(), u.getId());
            assertEquals(user.getEmail(), u.getEmail());
            assertEquals(user.getSalt(), u.getSalt());
            assertEquals(user.getHash(), u.getHash());
            assertEquals(user.getProtectedSymmetricKey(), u.getProtectedSymmetricKey());
            assertEquals(user.getInitializationVector(), u.getInitializationVector());
            assertEquals(getLocalDataTime(user.getTimestampCreation()), getLocalDataTime(u.getTimestampCreation()));
            assertEquals(getLocalDataTime(user.getTimestampLastAccess()), getLocalDataTime(u.getTimestampLastAccess()));
            assertEquals(getLocalDataTime(user.getTimestampPassword()), getLocalDataTime(u.getTimestampPassword()));
            assertEquals(getLocalDataTime(user.getTimestampEmail()), getLocalDataTime(u.getTimestampEmail()));
            assertEquals(user.getLanguage(), u.getLanguage());
            assertEquals(user.getHint(), u.getHint());
            assertEquals(user.getPropic(), u.getPropic());
            assertEquals(UserStateEnum.VERIFIED, u.getState());
            assertNull(u.getVerificationCode());
            assertNull(u.getNewEmail());
            assertNull(u.getAttempt());
        }, Assert::fail);
    }

    @Test
    void testEmailAlreadyConfirmed() throws Exception {
        var user = signUp(ConfirmEmailTest.EMAIL, PASSWORD);
        var mockHttpServletRequestBuilder = patch(CONFIRM_EMAIL_URL, EMAIL, user.getVerificationCode())
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(mockHttpServletRequestBuilder)
                .andExpect(status().isOk());

        mockMvc.perform(mockHttpServletRequestBuilder)
                .andExpect(status().isNotFound());
    }

}
