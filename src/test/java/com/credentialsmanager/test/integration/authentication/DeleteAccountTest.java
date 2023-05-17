package com.credentialsmanager.test.integration.authentication;

import com.credentialsmanager.constants.UserStateEnum;
import com.credentialsmanager.test.ApiTest;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class DeleteAccountTest extends ApiTest {

    @Test
    void testWithoutToken() throws Exception {
        var mockHttpServletRequestBuilder = delete(DELETE_ACCOUNT_URL)
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(mockHttpServletRequestBuilder)
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testUserNotFoundForEmail() throws Exception {
        var user = signUp(EMAIL, PASSWORD);
        user = confirmEmail(EMAIL);
        var token = getTokenFromLogIn(EMAIL, PASSWORD);

        user.setEmail(EMAIL + ".");
        userRepository.save(user);

        var mockHttpServletRequestBuilder = delete(DELETE_ACCOUNT_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .header(AUTH_HEADER_NAME, AUTH_HEADER_PREFIX + token);

        mockMvc.perform(mockHttpServletRequestBuilder)
                .andExpect(status().isNotFound());
    }

    @Test
    void testUserNotFoundForState() throws Exception {
        var user = signUp(EMAIL, PASSWORD);
        user = confirmEmail(EMAIL);
        var token = getTokenFromLogIn(EMAIL, PASSWORD);

        user.setState(UserStateEnum.UNVERIFIED);
        userRepository.save(user);

        var mockHttpServletRequestBuilder = delete(DELETE_ACCOUNT_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .header(AUTH_HEADER_NAME, AUTH_HEADER_PREFIX + token);

        mockMvc.perform(mockHttpServletRequestBuilder)
                .andExpect(status().isNotFound());
    }

    @Test
    void testDeleteUser() throws Exception {
        signUp(EMAIL, PASSWORD);
        confirmEmail(EMAIL);

        var mockHttpServletRequestBuilder = delete(DELETE_ACCOUNT_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .header(AUTH_HEADER_NAME, AUTH_HEADER_PREFIX + getTokenFromLogIn(EMAIL, PASSWORD));

        mockMvc.perform(mockHttpServletRequestBuilder)
                .andExpect(status().isOk());

        assertTrue(userRepository.findByEmail(EMAIL).isEmpty());

        //Check email
        var receivedMessages = greenMail.getReceivedMessages();
        assertTrue(greenMail.waitForIncomingEmail(5000, 1));
        assertEquals(3, receivedMessages.length);

        var email = receivedMessages[2];
        assertEquals(1, email.getAllRecipients().length);
        assertEquals(emailFrom, email.getFrom()[0].toString());
        assertEquals(EMAIL, email.getAllRecipients()[0].toString());
        assertEquals("Successfully deleted your Credential Manager account!", email.getSubject());
    }

}