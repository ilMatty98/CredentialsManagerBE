package com.credentialsmanager.test.integration.authentication;

import com.credentialsmanager.constants.MessageEnum;
import com.credentialsmanager.constants.UserStateEnum;
import com.credentialsmanager.dto.LogInDto;
import com.credentialsmanager.test.ApiTest;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class LogInTest extends ApiTest {

    @Test
    void testLogInDtoEmpty() throws Exception {
        var mockHttpServletRequestBuilder = post(LOG_IN_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectToJsonString(new LogInDto.Request()));

        mockMvc.perform(mockHttpServletRequestBuilder)
                .andExpect(status().isBadRequest());
    }

    @Test
    void testEmailEmpty() throws Exception {
        var logIn = fillObject(new LogInDto.Request());
        logIn.setEmail(null);

        var mockHttpServletRequestBuilder = post(LOG_IN_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectToJsonString(logIn));

        mockMvc.perform(mockHttpServletRequestBuilder)
                .andExpect(status().isBadRequest());
    }

    @Test
    void testEmailNotValid() throws Exception {
        var logIn = fillObject(new LogInDto.Request());

        var mockHttpServletRequestBuilder = post(LOG_IN_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectToJsonString(logIn));

        mockMvc.perform(mockHttpServletRequestBuilder)
                .andExpect(status().isBadRequest());
    }

    @Test
    void testMasterPasswordHashEmpty() throws Exception {
        var logIn = fillObject(new LogInDto.Request());
        logIn.setEmail(EMAIL);
        logIn.setMasterPasswordHash(null);

        var mockHttpServletRequestBuilder = post(LOG_IN_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectToJsonString(logIn));

        mockMvc.perform(mockHttpServletRequestBuilder)
                .andExpect(status().isBadRequest());
    }

    @Test
    void testIpAddressEmpty() throws Exception {
        var logIn = fillObject(new LogInDto.Request());
        logIn.setEmail(EMAIL);
        logIn.setIpAddress(null);

        var mockHttpServletRequestBuilder = post(LOG_IN_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectToJsonString(logIn));

        mockMvc.perform(mockHttpServletRequestBuilder)
                .andExpect(status().isBadRequest());
    }

    @Test
    void testIpAddressNotValid() throws Exception {
        var logIn = fillObject(new LogInDto.Request());
        logIn.setEmail(EMAIL);
        logIn.setIpAddress("fakeip");

        var mockHttpServletRequestBuilder = post(LOG_IN_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectToJsonString(logIn));

        mockMvc.perform(mockHttpServletRequestBuilder)
                .andExpect(status().isBadRequest());
    }

    @Test
    void testDeviceTypeEmpty() throws Exception {
        var logIn = fillObject(new LogInDto.Request());
        logIn.setEmail(EMAIL);
        logIn.setIpAddress(IP_ADDRESS);
        logIn.setDeviceType(null);

        var mockHttpServletRequestBuilder = post(LOG_IN_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectToJsonString(logIn));

        mockMvc.perform(mockHttpServletRequestBuilder)
                .andExpect(status().isBadRequest());
    }

    @Test
    void testLocalDateTimeEmpty() throws Exception {
        var logIn = fillObject(new LogInDto.Request());
        logIn.setEmail(EMAIL);
        logIn.setIpAddress(IP_ADDRESS);
        logIn.setLocalDateTime(null);

        var mockHttpServletRequestBuilder = post(LOG_IN_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectToJsonString(logIn));

        mockMvc.perform(mockHttpServletRequestBuilder)
                .andExpect(status().isBadRequest());
    }

    @Test
    void testUserNotFound() throws Exception {
        signUp(EMAIL, PASSWORD);
        var logIn = fillObject(new LogInDto.Request());
        logIn.setEmail("a" + EMAIL);
        logIn.setIpAddress(IP_ADDRESS);

        var mockHttpServletRequestBuilder = post(LOG_IN_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectToJsonString(logIn));

        mockMvc.perform(mockHttpServletRequestBuilder)
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath(MESSAGE).value(MessageEnum.ERROR_02.getLabel()));
    }

    @Test
    void testUserUnverified() throws Exception {
        var user = signUp(EMAIL, PASSWORD);
        user.setState(UserStateEnum.UNVERIFIED);
        user = userRepository.save(user);

        var logIn = fillObject(new LogInDto.Request());
        logIn.setEmail(user.getEmail());
        logIn.setIpAddress(IP_ADDRESS);

        var mockHttpServletRequestBuilder = post(LOG_IN_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectToJsonString(logIn));

        mockMvc.perform(mockHttpServletRequestBuilder)
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath(MESSAGE).value(MessageEnum.ERROR_06.getLabel()));
    }

    @Test
    void testMasterPasswordHashDifferent() throws Exception {
        var logIn = fillObject(new LogInDto.Request());
        logIn.setEmail(EMAIL);
        logIn.setIpAddress(IP_ADDRESS);
        logIn.setMasterPasswordHash(PASSWORD + ".");

        signUp(EMAIL, PASSWORD);
        confirmEmail(EMAIL);

        var mockHttpServletRequestBuilder = post(LOG_IN_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectToJsonString(logIn));

        mockMvc.perform(mockHttpServletRequestBuilder)
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath(MESSAGE).value(MessageEnum.ERROR_02.getLabel()));
    }

    @Test
    void testLogIn() throws Exception {
        var logIn = fillObject(new LogInDto.Request());
        logIn.setEmail(EMAIL);
        logIn.setIpAddress(IP_ADDRESS);
        logIn.setMasterPasswordHash(PASSWORD);

        var user = signUp(EMAIL, PASSWORD);
        user = confirmEmail(EMAIL);

        var mockHttpServletRequestBuilder = post(LOG_IN_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectToJsonString(logIn));

        mockMvc.perform(mockHttpServletRequestBuilder)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isNotEmpty())
                .andExpect(jsonPath("$.tokenPublicKey").value(tokenPublicKey))
                .andExpect(jsonPath("$.protectedSymmetricKey").value(authenticationMapper.base64DecodingString(user.getProtectedSymmetricKey())))
                .andExpect(jsonPath("$.initializationVector").value(authenticationMapper.base64DecodingString(user.getInitializationVector())))
                .andExpect(jsonPath("$.language").value(user.getLanguage()))
                .andExpect(jsonPath("$.timestampCreation").isNotEmpty())
                .andExpect(jsonPath("$.timestampLastAccess").isNotEmpty())
                .andExpect(jsonPath("$.timestampPassword").isNotEmpty());

        user = userRepository.findByEmail(EMAIL).orElseThrow(RuntimeException::new);
        assertTrue(user.getTimestampLastAccess().after(user.getTimestampCreation()));

        //Check email
        var receivedMessages = greenMail.getReceivedMessages();
        assertTrue(greenMail.waitForIncomingEmail(5000, 1));
        assertEquals(2, receivedMessages.length);

        var email = receivedMessages[1];
        assertEquals(1, email.getAllRecipients().length);
        assertEquals(emailFrom, email.getFrom()[0].toString());
        assertEquals(EMAIL, email.getAllRecipients()[0].toString());
        assertEquals("New access on Credential Manager!", email.getSubject());
    }

}
