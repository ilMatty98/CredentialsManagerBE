package com.credentialsmanager.test.integration;

import com.credentialsmanager.constants.TokenClaimEnum;
import com.credentialsmanager.constants.UserStateEnum;
import com.credentialsmanager.dto.SignUpDto;
import com.credentialsmanager.test.ApiTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import java.util.HashMap;

import static com.credentialsmanager.constants.UrlConstants.BASE_PATH;
import static com.credentialsmanager.constants.UrlConstants.CHANGE_PASSWORD;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ChangePasswordTest extends ApiTest {

    private static final String CHANGE_PASSWORD_URL = BASE_PATH + CHANGE_PASSWORD;
    private static final String AUTH_HEADER_NAME = "Authorization";
    private static final String AUTH_HEADER_PREFIX = "Bearer ";
    private static final String EMAIL = "test@test.com";
    private static final String PASSWORD = "password";

    @Test
    void testWithoutToken() throws Exception {
        var mockHttpServletRequestBuilder = post(CHANGE_PASSWORD_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectToJsonString(new SignUpDto()));

        mockMvc.perform(mockHttpServletRequestBuilder)
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testSignUpDtoEmpty() throws Exception {
        signUp(EMAIL, PASSWORD);
        confirmEmail(EMAIL);

        var mockHttpServletRequestBuilder = post(CHANGE_PASSWORD_URL)
                .header(AUTH_HEADER_NAME, AUTH_HEADER_PREFIX + getTokenFromLogIn(EMAIL, PASSWORD))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectToJsonString(new SignUpDto()));

        mockMvc.perform(mockHttpServletRequestBuilder)
                .andExpect(status().isBadRequest());
    }

    @Test
    void testMasterPasswordHashEmpty() throws Exception {
        signUp(EMAIL, PASSWORD);
        confirmEmail(EMAIL);

        var signUp = fillObject(new SignUpDto());
        signUp.setEmail(EMAIL);
        signUp.setMasterPasswordHash(null);

        var mockHttpServletRequestBuilder = post(CHANGE_PASSWORD_URL)
                .header(AUTH_HEADER_NAME, AUTH_HEADER_PREFIX + getTokenFromLogIn(EMAIL, PASSWORD))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectToJsonString(signUp));

        mockMvc.perform(mockHttpServletRequestBuilder)
                .andExpect(status().isBadRequest());
    }

    @Test
    void testProtectedSymmetricKeyEmpty() throws Exception {
        signUp(EMAIL, PASSWORD);
        confirmEmail(EMAIL);

        var signUp = fillObject(new SignUpDto());
        signUp.setEmail(EMAIL);
        signUp.setProtectedSymmetricKey(null);

        var mockHttpServletRequestBuilder = post(CHANGE_PASSWORD_URL)
                .header(AUTH_HEADER_NAME, AUTH_HEADER_PREFIX + getTokenFromLogIn(EMAIL, PASSWORD))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectToJsonString(signUp));

        mockMvc.perform(mockHttpServletRequestBuilder)
                .andExpect(status().isBadRequest());
    }

    @Test
    void testInitializationVectorEmpty() throws Exception {
        signUp(EMAIL, PASSWORD);
        confirmEmail(EMAIL);

        var signUp = fillObject(new SignUpDto());
        signUp.setEmail(EMAIL);
        signUp.setInitializationVector(null);

        var mockHttpServletRequestBuilder = post(CHANGE_PASSWORD_URL)
                .header(AUTH_HEADER_NAME, AUTH_HEADER_PREFIX + getTokenFromLogIn(EMAIL, PASSWORD))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectToJsonString(signUp));

        mockMvc.perform(mockHttpServletRequestBuilder)
                .andExpect(status().isBadRequest());
    }

    @Test
    void testWithClaimsWithoutEmail() throws Exception {
        var user = signUp(EMAIL, PASSWORD);
        user = confirmEmail(EMAIL);

        var signUp = fillObject(new SignUpDto());
        signUp.setMasterPasswordHash("new password");
        signUp.setProtectedSymmetricKey("new protectedSymmetricKey");
        signUp.setInitializationVector("new initializationVector");

        var claims = new HashMap<String, Object>();
        claims.put(TokenClaimEnum.ROLE.getLabel(), user.getState());
        var token = tokenJwtService.generateTokenJwt(5, EMAIL, claims);

        var mockHttpServletRequestBuilder = post(CHANGE_PASSWORD_URL)
                .header(AUTH_HEADER_NAME, AUTH_HEADER_PREFIX + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectToJsonString(signUp));

        mockMvc.perform(mockHttpServletRequestBuilder)
                .andExpect(status().isNotFound());
    }

    @Test
    void testEmailNotFound() throws Exception {
        var user = signUp(EMAIL, PASSWORD);
        user = confirmEmail(EMAIL);

        var signUp = fillObject(new SignUpDto());
        signUp.setMasterPasswordHash("new password");
        signUp.setProtectedSymmetricKey("new protectedSymmetricKey");
        signUp.setInitializationVector("new initializationVector");

        var claims = new HashMap<String, Object>();
        claims.put(TokenClaimEnum.EMAIL.getLabel(), EMAIL + ".");
        claims.put(TokenClaimEnum.ROLE.getLabel(), user.getState());
        var token = tokenJwtService.generateTokenJwt(5, EMAIL, claims);

        var mockHttpServletRequestBuilder = post(CHANGE_PASSWORD_URL)
                .header(AUTH_HEADER_NAME, AUTH_HEADER_PREFIX + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectToJsonString(signUp));

        mockMvc.perform(mockHttpServletRequestBuilder)
                .andExpect(status().isNotFound());
    }

    @Test
    void testChangePassword() throws Exception {
        signUp(EMAIL, PASSWORD);
        final var user = confirmEmail(EMAIL);

        var signUp = new SignUpDto();
        signUp.setEmail(EMAIL);
        signUp.setLanguage("EN");
        signUp.setMasterPasswordHash("new password");
        signUp.setProtectedSymmetricKey("new protectedSymmetricKey");
        signUp.setInitializationVector("new initializationVector");

        var mockHttpServletRequestBuilder = post(CHANGE_PASSWORD_URL)
                .header(AUTH_HEADER_NAME, AUTH_HEADER_PREFIX + getTokenFromLogIn(EMAIL, PASSWORD))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectToJsonString(signUp));

        mockMvc.perform(mockHttpServletRequestBuilder)
                .andExpect(status().isOk());

        userRepository.findByEmail(EMAIL)
                .ifPresentOrElse(u -> {
                    assertNotNull(u.getId());
                    assertEquals(signUp.getEmail(), u.getEmail());
                    assertNotNull(u.getSalt());
                    assertNotNull(u.getHash());
                    assertEquals(signUp.getProtectedSymmetricKey(), authenticationMapper.base64DecodingString(u.getProtectedSymmetricKey()));
                    assertEquals(signUp.getInitializationVector(), authenticationMapper.base64DecodingString(u.getInitializationVector()));
                    assertNotNull(u.getTimestampCreation());
                    assertNotNull(u.getTimestampLastAccess());
                    assertNotNull(u.getTimestampPassword());
                    assertEquals(signUp.getLanguage(), u.getLanguage());
                    assertEquals(UserStateEnum.VERIFIED, u.getState());
                    assertNull(u.getVerificationCode());
                    assertTrue(u.getTimestampPassword().after(user.getTimestampPassword()));
                }, Assertions::fail);

        //Check email
        var receivedMessages = greenMail.getReceivedMessages();
        assertTrue(greenMail.waitForIncomingEmail(5000, 1));
        assertEquals(3, receivedMessages.length);

        var email = receivedMessages[2];
        assertEquals(1, email.getAllRecipients().length);
        assertEquals(emailFrom, email.getFrom()[0].toString());
        assertEquals(signUp.getEmail(), email.getAllRecipients()[0].toString());
        assertEquals("Password changed!", email.getSubject());
    }
}
