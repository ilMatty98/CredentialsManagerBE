package com.credentialsmanager.test.integration;

import com.credentialsmanager.dto.SignUpDto;
import com.credentialsmanager.test.ApiTest;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import static com.credentialsmanager.constants.UrlConstants.BASE_PATH;
import static com.credentialsmanager.constants.UrlConstants.CHECK_EMAIL;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class CheckEmailTest extends ApiTest {

    private static final String CHECK_EMAIL_URL = BASE_PATH + CHECK_EMAIL;

    @Test
    void testWithoutHeader() throws Exception {
        var mockHttpServletRequestBuilder = get(CHECK_EMAIL_URL)
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(mockHttpServletRequestBuilder)
                .andExpect(status().isBadRequest());
    }

    @Test
    void testEmailPresent() throws Exception {
        var signUp = fillObject(new SignUpDto());
        signUp.setEmail("test@test.com");
        signUp.setLanguage("IT");
        addUser(signUp.getEmail());

        var mockHttpServletRequestBuilder = get(CHECK_EMAIL_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .header("checkEmail", signUp.getEmail());

        mockMvc.perform(mockHttpServletRequestBuilder)
                .andExpect(status().isOk())
                .andExpect(content().string("true"));
    }

    @Test
    void testEmailNotPresent() throws Exception {
        var signUp = fillObject(new SignUpDto());
        signUp.setEmail("test@test.com");
        signUp.setLanguage("IT");
        addUser(signUp.getEmail());

        var mockHttpServletRequestBuilder = get(CHECK_EMAIL_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .header("checkEmail", signUp.getEmail() + "fake");

        mockMvc.perform(mockHttpServletRequestBuilder)
                .andExpect(status().isOk())
                .andExpect(content().string("false"));
    }
}
