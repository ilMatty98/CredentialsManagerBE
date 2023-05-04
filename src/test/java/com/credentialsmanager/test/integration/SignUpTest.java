package com.credentialsmanager.test.integration;

import com.credentialsmanager.dto.SignUpDto;
import com.credentialsmanager.test.ApiTest;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import static com.credentialsmanager.constants.UrlConstants.BASE_PATH;
import static com.credentialsmanager.constants.UrlConstants.SIGN_UP;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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

}
