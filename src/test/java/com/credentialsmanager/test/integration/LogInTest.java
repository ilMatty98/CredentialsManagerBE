package com.credentialsmanager.test.integration;

import com.credentialsmanager.constants.MessageEnum;
import com.credentialsmanager.constants.UserStateEnum;
import com.credentialsmanager.dto.LogInDto;
import com.credentialsmanager.test.ApiTest;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import static com.credentialsmanager.constants.UrlConstants.BASE_PATH;
import static com.credentialsmanager.constants.UrlConstants.LOG_IN;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class LogInTest extends ApiTest {

    private static final String LOG_IN_URL = BASE_PATH + LOG_IN;

    private static final String EMAIL = "test@test.com";

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
        logIn.setEmail("test@test.com");
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
        logIn.setEmail("test@test.com");
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
        logIn.setEmail("test@test.com");
        logIn.setIpAddress("asdasdasdasd");

        var mockHttpServletRequestBuilder = post(LOG_IN_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectToJsonString(logIn));

        mockMvc.perform(mockHttpServletRequestBuilder)
                .andExpect(status().isBadRequest());
    }

    @Test
    void testDeviceTypeEmpty() throws Exception {
        var logIn = fillObject(new LogInDto.Request());
        logIn.setEmail("test@test.com");
        logIn.setIpAddress("1.1.1.1");
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
        logIn.setEmail("test@test.com");
        logIn.setIpAddress("1.1.1.1");
        logIn.setLocalDateTime(null);

        var mockHttpServletRequestBuilder = post(LOG_IN_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectToJsonString(logIn));

        mockMvc.perform(mockHttpServletRequestBuilder)
                .andExpect(status().isBadRequest());
    }

    @Test
    void testUserNotFound() throws Exception {
        addUser(EMAIL);
        var logIn = fillObject(new LogInDto.Request());
        logIn.setEmail("test1@test.com");
        logIn.setIpAddress("1.1.1.1");

        var mockHttpServletRequestBuilder = post(LOG_IN_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectToJsonString(logIn));

        mockMvc.perform(mockHttpServletRequestBuilder)
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath(MESSAGE).value(MessageEnum.ERROR_02.getLabel()));
    }

    @Test
    void testUserUnverified() throws Exception {
        var user = addUser(EMAIL);
        user.setState(UserStateEnum.UNVERIFIED);
        user = userRepository.save(user);

        var logIn = fillObject(new LogInDto.Request());
        logIn.setEmail(user.getEmail());
        logIn.setIpAddress("1.1.1.1");

        var mockHttpServletRequestBuilder = post(LOG_IN_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectToJsonString(logIn));

        mockMvc.perform(mockHttpServletRequestBuilder)
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath(MESSAGE).value(MessageEnum.ERROR_06.getLabel()));
    }

    @Test
    void testMasterPasswordHashDifferent() throws Exception {
        var user = addUser(EMAIL);
        user = userRepository.save(user);

        var logIn = fillObject(new LogInDto.Request());
        logIn.setEmail(user.getEmail());
        logIn.setIpAddress("1.1.1.1");

        var mockHttpServletRequestBuilder = post(LOG_IN_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectToJsonString(logIn));

        mockMvc.perform(mockHttpServletRequestBuilder)
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath(MESSAGE).value(MessageEnum.ERROR_06.getLabel()));
    }

}
