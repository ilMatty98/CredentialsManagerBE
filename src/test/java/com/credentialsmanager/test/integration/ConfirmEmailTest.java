package com.credentialsmanager.test.integration;

import com.credentialsmanager.constants.UserStateEnum;
import com.credentialsmanager.entity.User;
import com.credentialsmanager.test.ApiTest;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import static com.credentialsmanager.constants.UrlConstants.BASE_PATH;
import static com.credentialsmanager.constants.UrlConstants.CONFIRM_EMAIL;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ConfirmEmailTest extends ApiTest {

    private static final String CONFIRM_EMAIL_URL = BASE_PATH + CONFIRM_EMAIL;
    private static final String EMAIL = "email@emai.it";
    private static final String CODE = "code";

    @Test
    void testEmailNotFound() throws Exception {
        saveUser();
        var mockHttpServletRequestBuilder = patch(CONFIRM_EMAIL_URL, EMAIL + ".", CODE)
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(mockHttpServletRequestBuilder)
                .andExpect(status().isNotFound());
    }

    @Test
    void testCodeNotFound() throws Exception {
        saveUser();
        var mockHttpServletRequestBuilder = patch(CONFIRM_EMAIL_URL, EMAIL, CODE + ".")
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(mockHttpServletRequestBuilder)
                .andExpect(status().isNotFound());
    }

    @Test
    void testConfirmEmail() throws Exception {
        var user = saveUser();
        var mockHttpServletRequestBuilder = patch(CONFIRM_EMAIL_URL, EMAIL, CODE)
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
            assertEquals(user.getLanguage(), u.getLanguage());
            assertEquals(UserStateEnum.VERIFIED, u.getState());
            assertNull(u.getVerificationCode());
        }, Assert::fail);
    }

    @Test
    void testEmailAlreadyConfirmed() throws Exception {
        saveUser();
        var mockHttpServletRequestBuilder = patch(CONFIRM_EMAIL_URL, EMAIL, CODE)
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(mockHttpServletRequestBuilder)
                .andExpect(status().isOk());

        mockMvc.perform(mockHttpServletRequestBuilder)
                .andExpect(status().isNotFound());
    }

    private User saveUser() {
        var user = addUser(ConfirmEmailTest.EMAIL);
        user.setVerificationCode(ConfirmEmailTest.CODE);
        return userRepository.save(user);
    }

}
