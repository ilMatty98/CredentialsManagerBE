package com.credentialsmanager.test.integration;

import com.credentialsmanager.constants.MessageEnum;
import com.credentialsmanager.constants.UserStateEnum;
import com.credentialsmanager.dto.SignUpDto;
import com.credentialsmanager.test.ApiTest;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import static com.credentialsmanager.constants.UrlConstants.BASE_PATH;
import static com.credentialsmanager.constants.UrlConstants.SIGN_UP;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class SignUpTest extends ApiTest {

    private static final String SIGN_UP_URL = BASE_PATH + SIGN_UP;

    @Test
    void testSignUpDtoEmpty() throws Exception {
        var mockHttpServletRequestBuilder = post(SIGN_UP_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectToJsonString(new SignUpDto()));

        mockMvc.perform(mockHttpServletRequestBuilder).andExpect(status().isBadRequest());
    }

    @Test
    void testEmailEmpty() throws Exception {
        var signUp = fillObject(new SignUpDto());
        signUp.setEmail(null);

        var mockHttpServletRequestBuilder = post(SIGN_UP_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectToJsonString(signUp));

        mockMvc.perform(mockHttpServletRequestBuilder)
                .andExpect(status().isBadRequest());
    }

    @Test
    void testEmailNotValid() throws Exception {
        var signUp = fillObject(new SignUpDto());

        var mockHttpServletRequestBuilder = post(SIGN_UP_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectToJsonString(signUp));

        mockMvc.perform(mockHttpServletRequestBuilder)
                .andExpect(status().isBadRequest());
    }

    @Test
    void testMasterPasswordHashEmpty() throws Exception {
        var signUp = fillObject(new SignUpDto());
        signUp.setEmail("test@test.com");
        signUp.setMasterPasswordHash(null);

        var mockHttpServletRequestBuilder = post(SIGN_UP_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectToJsonString(signUp));

        mockMvc.perform(mockHttpServletRequestBuilder)
                .andExpect(status().isBadRequest());
    }

    @Test
    void testProtectedSymmetricKeyEmpty() throws Exception {
        var signUp = fillObject(new SignUpDto());
        signUp.setEmail("test@test.com");
        signUp.setProtectedSymmetricKey(null);

        var mockHttpServletRequestBuilder = post(SIGN_UP_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectToJsonString(signUp));

        mockMvc.perform(mockHttpServletRequestBuilder)
                .andExpect(status().isBadRequest());
    }

    @Test
    void testInitializationVectorEmpty() throws Exception {
        var signUp = fillObject(new SignUpDto());
        signUp.setEmail("test@test.com");
        signUp.setInitializationVector(null);

        var mockHttpServletRequestBuilder = post(SIGN_UP_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectToJsonString(signUp));

        mockMvc.perform(mockHttpServletRequestBuilder)
                .andExpect(status().isBadRequest());
    }

    @Test
    void testLanguageEmpty() throws Exception {
        var signUp = fillObject(new SignUpDto());
        signUp.setEmail("test@test.com");
        signUp.setLanguage(null);

        var mockHttpServletRequestBuilder = post(SIGN_UP_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectToJsonString(signUp));

        mockMvc.perform(mockHttpServletRequestBuilder)
                .andExpect(status().isBadRequest());
    }

    @Test
    void testLanguageNotValid() throws Exception {
        var signUp = fillObject(new SignUpDto());
        signUp.setEmail("test@test.com");
        signUp.setLanguage("asdasdasd");

        var mockHttpServletRequestBuilder = post(SIGN_UP_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectToJsonString(signUp));

        mockMvc.perform(mockHttpServletRequestBuilder)
                .andExpect(status().isBadRequest());
    }

    @Test
    void testEmailAlreadyRegistered() throws Exception {
        var signUp = fillObject(new SignUpDto());
        signUp.setEmail("test@test.com");
        signUp.setLanguage("IT");

        addUser(signUp.getEmail());

        var mockHttpServletRequestBuilder = post(SIGN_UP_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectToJsonString(signUp));

        mockMvc.perform(mockHttpServletRequestBuilder)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath(MESSAGE).value(MessageEnum.ERROR_01.getLabel()));
    }

    @Test
    void testSignUp() throws Exception {
        var signUp = fillObject(new SignUpDto());
        signUp.setEmail("test@test.com");
        signUp.setLanguage("EN");

        var mockHttpServletRequestBuilder = post(SIGN_UP_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectToJsonString(signUp));

        mockMvc.perform(mockHttpServletRequestBuilder)
                .andExpect(status().isCreated());

        //Check user
        userRepository.findByEmail(signUp.getEmail())
                .ifPresentOrElse(user -> {
                    assertNotNull(user.getId());
                    assertEquals(signUp.getEmail(), user.getEmail());
                    assertNotNull(user.getSalt());
                    assertNotNull(user.getHash());
                    assertEquals(signUp.getProtectedSymmetricKey(), authenticationMapper.base64DecodingString(user.getProtectedSymmetricKey()));
                    assertEquals(signUp.getInitializationVector(), authenticationMapper.base64DecodingString(user.getInitializationVector()));
                    assertNotNull(user.getTimestampCreation());
                    assertNotNull(user.getTimestampLastAccess());
                    assertNotNull(user.getTimestampPassword());
                    assertEquals(signUp.getLanguage(), user.getLanguage());
                    assertEquals(UserStateEnum.UNVERIFIED, user.getState());
                    assertNotNull(user.getVerificationCode());
                }, () -> fail("User not found"));

        //Check email
        var receivedMessages = greenMail.getReceivedMessages();
        assertTrue(greenMail.waitForIncomingEmail(5000, 1));
        assertEquals(1, receivedMessages.length);

        var email = receivedMessages[0];
        assertEquals(1, email.getAllRecipients().length);
        assertEquals(emailFrom, email.getFrom()[0].toString());
        assertEquals(signUp.getEmail(), email.getAllRecipients()[0].toString());
        assertEquals("Welcome to Credentials Manager!", receivedMessages[0].getSubject());
    }

}
