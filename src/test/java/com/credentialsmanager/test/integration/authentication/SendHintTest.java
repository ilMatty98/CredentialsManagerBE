package com.credentialsmanager.test.integration.authentication;

import com.credentialsmanager.constants.MessageEnum;
import com.credentialsmanager.constants.UserStateEnum;
import com.credentialsmanager.test.ApiTest;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import static com.credentialsmanager.constants.UrlConstants.HEADER_EMAIL;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class SendHintTest extends ApiTest {

    @Test
    void testWithoutHeader() throws Exception {
        var mockHttpServletRequestBuilder = post(SEND_HINT_URL)
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(mockHttpServletRequestBuilder)
                .andExpect(status().isBadRequest());
    }

    @Test
    void testNotFoundForEmail() throws Exception {
        var user = signUp(EMAIL, PASSWORD);
        user = confirmEmail(EMAIL);

        user.setEmail(EMAIL + ".");
        userRepository.save(user);

        var mockHttpServletRequestBuilder = post(SEND_HINT_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .header(HEADER_EMAIL, EMAIL);

        mockMvc.perform(mockHttpServletRequestBuilder)
                .andExpect(status().isNotFound())
                .andExpect(jsonPath(MESSAGE).value(MessageEnum.ERROR_03.getLabel()));
    }

    @Test
    void testNotFoundForState() throws Exception {
        var user = signUp(EMAIL, PASSWORD);
        user = confirmEmail(EMAIL);

        user.setState(UserStateEnum.UNVERIFIED);
        userRepository.save(user);

        var mockHttpServletRequestBuilder = post(SEND_HINT_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .header(HEADER_EMAIL, EMAIL);

        mockMvc.perform(mockHttpServletRequestBuilder)
                .andExpect(status().isNotFound())
                .andExpect(jsonPath(MESSAGE).value(MessageEnum.ERROR_03.getLabel()));
    }

    @Test
    void testSendHint() throws Exception {
        signUp(EMAIL, PASSWORD);
        confirmEmail(EMAIL);

        var mockHttpServletRequestBuilder = post(SEND_HINT_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .header(HEADER_EMAIL, EMAIL);

        mockMvc.perform(mockHttpServletRequestBuilder)
                .andExpect(status().isOk());

        //Check email
        var receivedMessages = greenMail.getReceivedMessages();
        assertTrue(greenMail.waitForIncomingEmail(5000, 1));
        assertEquals(2, receivedMessages.length);

        var email = receivedMessages[1];
        assertEquals(1, email.getAllRecipients().length);
        assertEquals(emailFrom, email.getFrom()[0].toString());
        assertEquals(EMAIL, email.getAllRecipients()[0].toString());
        assertEquals("Your Master Password Hint", email.getSubject());
    }

}
