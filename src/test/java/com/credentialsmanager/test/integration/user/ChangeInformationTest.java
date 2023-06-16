package com.credentialsmanager.test.integration.user;

import com.credentialsmanager.constants.UserStateEnum;
import com.credentialsmanager.dto.request.ChangeInformationDto;
import com.credentialsmanager.test.ApiTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ChangeInformationTest extends ApiTest {

    @Test
    void testWithoutToken() throws Exception {
        var mockHttpServletRequestBuilder = put(CHANGE_INFORMATION_URL)
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(mockHttpServletRequestBuilder)
                .andExpect(status().isBadRequest());
    }

    @Test
    void testLanguageEmpty() throws Exception {
        signUp(EMAIL, PASSWORD);
        confirmEmail(EMAIL);
        var mockHttpServletRequestBuilder = put(CHANGE_INFORMATION_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .header(AUTH_HEADER_NAME, AUTH_HEADER_PREFIX + getTokenFromLogIn(EMAIL, PASSWORD))
                .content(objectToJsonString(new ChangeInformationDto()));

        mockMvc.perform(mockHttpServletRequestBuilder)
                .andExpect(status().isBadRequest());
    }

    @Test
    void testLanguageNotValid() throws Exception {
        var changeInformationDto = new ChangeInformationDto();
        changeInformationDto.setLanguage("AAA");

        signUp(EMAIL, PASSWORD);
        confirmEmail(EMAIL);
        var mockHttpServletRequestBuilder = put(CHANGE_INFORMATION_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .header(AUTH_HEADER_NAME, AUTH_HEADER_PREFIX + getTokenFromLogIn(EMAIL, PASSWORD))
                .content(objectToJsonString(changeInformationDto));

        mockMvc.perform(mockHttpServletRequestBuilder)
                .andExpect(status().isBadRequest());
    }

    @Test
    void testHintTooLong() throws Exception {
        var changeInformationDto = new ChangeInformationDto();
        changeInformationDto.setLanguage(EN);
        changeInformationDto.setHint(generateRandomString(101));

        signUp(EMAIL, PASSWORD);
        confirmEmail(EMAIL);
        var mockHttpServletRequestBuilder = put(CHANGE_INFORMATION_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .header(AUTH_HEADER_NAME, AUTH_HEADER_PREFIX + getTokenFromLogIn(EMAIL, PASSWORD))
                .content(objectToJsonString(changeInformationDto));

        mockMvc.perform(mockHttpServletRequestBuilder)
                .andExpect(status().isBadRequest());
    }

    @Test
    void testPropicEmpty() throws Exception {
        var changeInformationDto = new ChangeInformationDto();
        changeInformationDto.setLanguage("FR");
        changeInformationDto.setHint("hint");
        changeInformationDto.setPropic(null);

        signUp(EMAIL, PASSWORD);
        confirmEmail(EMAIL);
        var mockHttpServletRequestBuilder = put(CHANGE_INFORMATION_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .header(AUTH_HEADER_NAME, AUTH_HEADER_PREFIX + getTokenFromLogIn(EMAIL, PASSWORD))
                .content(objectToJsonString(changeInformationDto));

        mockMvc.perform(mockHttpServletRequestBuilder)
                .andExpect(status().isBadRequest());
    }

    @Test
    void testUserNotFoundForEmail() throws Exception {
        var user = signUp(EMAIL, PASSWORD);
        user = confirmEmail(EMAIL);
        var token = getTokenFromLogIn(EMAIL, PASSWORD);

        var changeInformationDto = new ChangeInformationDto();
        changeInformationDto.setLanguage("FR");
        changeInformationDto.setHint("hint");
        changeInformationDto.setPropic("propic");

        user.setEmail(EMAIL + ".");
        userRepository.save(user);

        var mockHttpServletRequestBuilder = put(CHANGE_INFORMATION_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .header(AUTH_HEADER_NAME, AUTH_HEADER_PREFIX + token)
                .content(objectToJsonString(changeInformationDto));

        mockMvc.perform(mockHttpServletRequestBuilder)
                .andExpect(status().isNotFound());
    }

    @Test
    void testUserNotFoundForState() throws Exception {
        var user = signUp(EMAIL, PASSWORD);
        user = confirmEmail(EMAIL);
        var token = getTokenFromLogIn(EMAIL, PASSWORD);

        var changeInformationDto = new ChangeInformationDto();
        changeInformationDto.setLanguage("FR");
        changeInformationDto.setHint("hint");
        changeInformationDto.setPropic("propic");

        user.setState(UserStateEnum.UNVERIFIED);
        userRepository.save(user);

        var mockHttpServletRequestBuilder = put(CHANGE_INFORMATION_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .header(AUTH_HEADER_NAME, AUTH_HEADER_PREFIX + token)
                .content(objectToJsonString(changeInformationDto));

        mockMvc.perform(mockHttpServletRequestBuilder)
                .andExpect(status().isNotFound());
    }

    @Test
    void testMaxSizePropic() throws Exception {
        signUp(EMAIL, PASSWORD);
        confirmEmail(EMAIL);

        var changeInformationDto = new ChangeInformationDto();
        changeInformationDto.setLanguage("FR");
        changeInformationDto.setHint("new hint");
        changeInformationDto.setPropic(createLargeString(4.1));

        var mockHttpServletRequestBuilder = put(CHANGE_INFORMATION_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .header(AUTH_HEADER_NAME, AUTH_HEADER_PREFIX + getTokenFromLogIn(EMAIL, PASSWORD))
                .content(objectToJsonString(changeInformationDto));

        mockMvc.perform(mockHttpServletRequestBuilder)
                .andExpect(status().isBadRequest());
    }

    @Test
    void testChangeInformation() throws Exception {
        signUp(EMAIL, PASSWORD);
        final var user = confirmEmail(EMAIL);

        var changeInformationDto = new ChangeInformationDto();
        changeInformationDto.setLanguage("FR");
        changeInformationDto.setHint("new hint");
        changeInformationDto.setPropic("new propic");

        var mockHttpServletRequestBuilder = put(CHANGE_INFORMATION_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .header(AUTH_HEADER_NAME, AUTH_HEADER_PREFIX + getTokenFromLogIn(EMAIL, PASSWORD))
                .content(objectToJsonString(changeInformationDto));

        mockMvc.perform(mockHttpServletRequestBuilder)
                .andExpect(status().isOk());

        //Check user
        userRepository.findByEmail(EMAIL)
                .ifPresentOrElse(u -> {
                    assertNotNull(u.getId());
                    assertEquals(EMAIL, u.getEmail());
                    assertNotNull(u.getSalt());
                    assertNotNull(u.getHash());
                    assertEquals(user.getProtectedSymmetricKey(), u.getProtectedSymmetricKey());
                    assertEquals(user.getInitializationVector(), u.getInitializationVector());
                    assertEquals(getLocalDataTime(user.getTimestampCreation()), getLocalDataTime(u.getTimestampCreation()));
                    assertEquals(getLocalDataTime(user.getTimestampPassword()), getLocalDataTime(u.getTimestampPassword()));
                    assertTrue(user.getTimestampLastAccess().before(u.getTimestampLastAccess()));
                    assertEquals(changeInformationDto.getLanguage(), u.getLanguage());
                    assertEquals(changeInformationDto.getHint(), u.getHint());
                    assertEquals(changeInformationDto.getPropic(), u.getPropic());
                    assertEquals(user.getState(), u.getState());
                    assertNull(u.getVerificationCode());
                }, Assertions::fail);
    }
}
