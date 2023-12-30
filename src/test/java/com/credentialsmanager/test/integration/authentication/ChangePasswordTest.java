package com.credentialsmanager.test.integration.authentication;

import com.credentialsmanager.constants.TokenClaimEnum;
import com.credentialsmanager.constants.UserStateEnum;
import com.credentialsmanager.dto.request.ChangePasswordDto;
import com.credentialsmanager.test.CredentialsManagerTests;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ChangePasswordTest extends CredentialsManagerTests {

    @Test
    void testWithoutToken() throws Exception {
        var mockHttpServletRequestBuilder = put(CHANGE_PASSWORD_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectToJsonString(new ChangePasswordDto()));

        mockMvc.perform(mockHttpServletRequestBuilder)
                .andExpect(status().isBadRequest());
    }

    @Test
    void testSignUpDtoEmpty() throws Exception {
        signUp(EMAIL, PASSWORD);
        confirmEmail(EMAIL);

        var mockHttpServletRequestBuilder = put(CHANGE_PASSWORD_URL)
                .header(AUTH_HEADER_NAME, AUTH_HEADER_PREFIX + getTokenFromLogIn(EMAIL, PASSWORD))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectToJsonString(new ChangePasswordDto()));

        mockMvc.perform(mockHttpServletRequestBuilder)
                .andExpect(status().isBadRequest());
    }

    @Test
    void testMasterPasswordHashEmpty() throws Exception {
        signUp(EMAIL, PASSWORD);
        confirmEmail(EMAIL);

        var changePasswordDto = fillObject(new ChangePasswordDto());
        changePasswordDto.setNewMasterPasswordHash(null);

        var mockHttpServletRequestBuilder = put(CHANGE_PASSWORD_URL)
                .header(AUTH_HEADER_NAME, AUTH_HEADER_PREFIX + getTokenFromLogIn(EMAIL, PASSWORD))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectToJsonString(changePasswordDto));

        mockMvc.perform(mockHttpServletRequestBuilder)
                .andExpect(status().isBadRequest());
    }

    @Test
    void testProtectedSymmetricKeyEmpty() throws Exception {
        signUp(EMAIL, PASSWORD);
        confirmEmail(EMAIL);

        var changePasswordDto = fillObject(new ChangePasswordDto());
        changePasswordDto.setNewProtectedSymmetricKey(null);

        var mockHttpServletRequestBuilder = put(CHANGE_PASSWORD_URL)
                .header(AUTH_HEADER_NAME, AUTH_HEADER_PREFIX + getTokenFromLogIn(EMAIL, PASSWORD))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectToJsonString(changePasswordDto));

        mockMvc.perform(mockHttpServletRequestBuilder)
                .andExpect(status().isBadRequest());
    }

    @Test
    void testInitializationVectorEmpty() throws Exception {
        signUp(EMAIL, PASSWORD);
        confirmEmail(EMAIL);

        var changePasswordDto = fillObject(new ChangePasswordDto());
        changePasswordDto.setNewInitializationVector(null);

        var mockHttpServletRequestBuilder = put(CHANGE_PASSWORD_URL)
                .header(AUTH_HEADER_NAME, AUTH_HEADER_PREFIX + getTokenFromLogIn(EMAIL, PASSWORD))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectToJsonString(changePasswordDto));

        mockMvc.perform(mockHttpServletRequestBuilder)
                .andExpect(status().isBadRequest());
    }

    @Test
    void testWithClaimsWithoutEmail() throws Exception {
        var user = signUp(EMAIL, PASSWORD);
        user = confirmEmail(EMAIL);

        var changePasswordDto = fillObject(new ChangePasswordDto());
        changePasswordDto.setNewMasterPasswordHash("new password");
        changePasswordDto.setNewProtectedSymmetricKey("new protectedSymmetricKey");
        changePasswordDto.setNewInitializationVector("new initializationVector");

        var claims = new HashMap<String, Object>();
        claims.put(TokenClaimEnum.ROLE.getLabel(), user.getState());
        var token = tokenJwtService.generateTokenJwt(EMAIL, claims);

        var mockHttpServletRequestBuilder = put(CHANGE_PASSWORD_URL)
                .header(AUTH_HEADER_NAME, AUTH_HEADER_PREFIX + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectToJsonString(changePasswordDto));

        mockMvc.perform(mockHttpServletRequestBuilder)
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testEmailNotFound() throws Exception {
        var user = signUp(EMAIL, PASSWORD);
        user = confirmEmail(EMAIL);

        var changePasswordDto = fillObject(new ChangePasswordDto());
        changePasswordDto.setNewMasterPasswordHash("new password");
        changePasswordDto.setNewProtectedSymmetricKey("new protectedSymmetricKey");
        changePasswordDto.setNewInitializationVector("new initializationVector");

        var claims = new HashMap<String, Object>();
        claims.put(TokenClaimEnum.EMAIL.getLabel(), EMAIL + ".");
        claims.put(TokenClaimEnum.ROLE.getLabel(), user.getState());
        var token = tokenJwtService.generateTokenJwt(EMAIL, claims);

        var mockHttpServletRequestBuilder = put(CHANGE_PASSWORD_URL)
                .header(AUTH_HEADER_NAME, AUTH_HEADER_PREFIX + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectToJsonString(changePasswordDto));

        mockMvc.perform(mockHttpServletRequestBuilder)
                .andExpect(status().isNotFound());
    }

    @Test
    void testCheckPasswordFailed() throws Exception {
        signUp(EMAIL, PASSWORD);
        confirmEmail(EMAIL);

        var changePasswordDto = fillObject(new ChangePasswordDto());
        changePasswordDto.setCurrentMasterPasswordHash(PASSWORD + "1");
        changePasswordDto.setNewMasterPasswordHash("new password");
        changePasswordDto.setNewProtectedSymmetricKey("new protectedSymmetricKey");
        changePasswordDto.setNewInitializationVector("new initializationVector");

        var mockHttpServletRequestBuilder = put(CHANGE_PASSWORD_URL)
                .header(AUTH_HEADER_NAME, AUTH_HEADER_PREFIX + getTokenFromLogIn(EMAIL, PASSWORD))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectToJsonString(changePasswordDto));

        mockMvc.perform(mockHttpServletRequestBuilder)
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testChangePassword() throws Exception {
        signUp(EMAIL, PASSWORD);
        final var user = confirmEmail(EMAIL);

        var changePasswordDto = new ChangePasswordDto();
        changePasswordDto.setCurrentMasterPasswordHash(PASSWORD);
        changePasswordDto.setNewMasterPasswordHash("new password");
        changePasswordDto.setNewProtectedSymmetricKey("new protectedSymmetricKey");
        changePasswordDto.setNewInitializationVector("new initializationVector");

        var mockHttpServletRequestBuilder = put(CHANGE_PASSWORD_URL)
                .header(AUTH_HEADER_NAME, AUTH_HEADER_PREFIX + getTokenFromLogIn(EMAIL, PASSWORD))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectToJsonString(changePasswordDto));

        mockMvc.perform(mockHttpServletRequestBuilder)
                .andExpect(status().isOk());

        userRepository.findByEmail(EMAIL)
                .ifPresentOrElse(u -> {
                    assertNotNull(u.getId());
                    assertEquals(user.getEmail(), u.getEmail());
                    assertNotNull(u.getSalt());
                    assertNotNull(u.getHash());
                    assertEquals(changePasswordDto.getNewProtectedSymmetricKey(), authenticationMapper.base64DecodingString(u.getProtectedSymmetricKey()));
                    assertEquals(changePasswordDto.getNewInitializationVector(), authenticationMapper.base64DecodingString(u.getInitializationVector()));
                    assertTrue(u.getTimestampPassword().after(user.getTimestampPassword()));
                    assertNotNull(u.getTimestampCreation());
                    assertNotNull(u.getTimestampLastAccess());
                    assertNotNull(u.getTimestampPassword());
                    assertNotNull(u.getTimestampEmail());
                    assertEquals(user.getLanguage(), u.getLanguage());
                    assertEquals(user.getHint(), u.getHint());
                    assertEquals(user.getPropic(), u.getPropic());
                    assertEquals(UserStateEnum.VERIFIED, u.getState());
                    assertNull(u.getVerificationCode());
                    assertNull(u.getNewEmail());
                    assertNull(u.getAttempt());
                }, Assertions::fail);

        //Check email
        var receivedMessages = greenMail.getReceivedMessages();
        assertTrue(greenMail.waitForIncomingEmail(5000, 1));
        assertEquals(3, receivedMessages.length);

        var email = receivedMessages[2];
        assertEquals(1, email.getAllRecipients().length);
        assertEquals(emailFrom, email.getFrom()[0].toString());
        assertEquals(user.getEmail(), email.getAllRecipients()[0].toString());
        assertEquals("Password changed!", email.getSubject());

        assertNotNull(getTokenFromLogIn(EMAIL, "new password"));
    }
}
