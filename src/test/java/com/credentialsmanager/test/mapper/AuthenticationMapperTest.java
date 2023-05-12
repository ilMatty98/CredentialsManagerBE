package com.credentialsmanager.test.mapper;

import com.credentialsmanager.constants.UserStateEnum;
import com.credentialsmanager.dto.SignUpDto;
import com.credentialsmanager.entity.User;
import com.credentialsmanager.test.ApiTest;
import org.apache.commons.lang3.RandomUtils;
import org.junit.jupiter.api.Test;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.Base64;

import static org.junit.Assert.*;

class AuthenticationMapperTest extends ApiTest {

    @Test
    void testNewUser() {
        var signUpDto = fillObject(new SignUpDto());
        var salt = RandomUtils.nextBytes(128);
        var hash = RandomUtils.nextBytes(256);
        var timestamp = Timestamp.from(Instant.now());
        var userStateEnum = UserStateEnum.VERIFIED;

        var user = authenticationMapper.newUser(signUpDto, salt, hash, timestamp, userStateEnum);

        assertNull(user.getId());
        assertEquals(signUpDto.getEmail(), user.getEmail());
        assertEquals(authenticationMapper.base64Encoding(salt), user.getSalt());
        assertEquals(authenticationMapper.base64Encoding(hash), user.getHash());
        assertEquals(authenticationMapper.base64EncodingString(signUpDto.getProtectedSymmetricKey()), user.getProtectedSymmetricKey());
        assertEquals(authenticationMapper.base64EncodingString(signUpDto.getInitializationVector()), user.getInitializationVector());
        assertEquals(timestamp, user.getTimestampCreation());
        assertEquals(timestamp, user.getTimestampLastAccess());
        assertEquals(timestamp, user.getTimestampPassword());
        assertEquals(signUpDto.getLanguage(), user.getLanguage());
        assertEquals(userStateEnum, user.getState());
        assertNotNull(user.getVerificationCode());
    }

    @Test
    void testNewLoginDto() {
        var user = fillObject(new User());
        var token = generateRandomString(2048);
        var tokenPublicKey = generateRandomString(1024);

        var protectedSymmetricKey = user.getProtectedSymmetricKey();
        var initializationVector = user.getInitializationVector();
        user.setProtectedSymmetricKey(authenticationMapper.base64EncodingString(protectedSymmetricKey));
        user.setInitializationVector(authenticationMapper.base64EncodingString(initializationVector));

        var loginDto = authenticationMapper.newLoginDto(user, token, tokenPublicKey);

        assertEquals(token, loginDto.getToken());
        assertEquals(tokenPublicKey, loginDto.getTokenPublicKey());
        assertEquals(protectedSymmetricKey, loginDto.getProtectedSymmetricKey());
        assertEquals(initializationVector, loginDto.getInitializationVector());
        assertEquals(user.getLanguage(), loginDto.getLanguage());
        assertEquals(user.getTimestampCreation(), loginDto.getTimestampCreation());
        assertEquals(user.getTimestampLastAccess(), loginDto.getTimestampLastAccess());
        assertEquals(user.getTimestampPassword(), loginDto.getTimestampPassword());
    }

    @Test
    void testBase64Encoding() {
        // Test with non-null input
        var input1 = "Hello, world!".getBytes();
        var expectedOutput1 = Base64.getEncoder().encodeToString(input1);
        var actualOutput1 = authenticationMapper.base64Encoding(input1);
        assertEquals(expectedOutput1, actualOutput1);

        // Test with null input
        var actualOutput2 = authenticationMapper.base64Encoding(null);
        assertNull(actualOutput2);
    }

    @Test
    void testBase64Decoding() {
        // Test with non-null input
        var input = "SGVsbG8gV29ybGQh";
        var expectedOutput1 = "Hello World!".getBytes();
        var actualOutput1 = authenticationMapper.base64Decoding(input);
        assertArrayEquals(expectedOutput1, actualOutput1);

        // Test with null input
        var actualOutput2 = authenticationMapper.base64Decoding(null);
        assertNull(actualOutput2);
    }

    @Test
    void testBase64EncodingString() {
        // Test with non-null input
        var input = "Hello World!";
        var expectedOutput1 = "SGVsbG8gV29ybGQh";
        var actualOutput1 = authenticationMapper.base64EncodingString(input);
        assertEquals(expectedOutput1, actualOutput1);

        // Test with null input
        var actualOutput2 = authenticationMapper.base64EncodingString(null);
        assertNull(actualOutput2);
    }

    @Test
    void testBase64DecodingString() {
        // Test with non-null input
        var input = "SGVsbG8gV29ybGQh";
        var expectedOutput1 = "Hello World!";
        var actualOutput1 = authenticationMapper.base64DecodingString(input);
        assertEquals(expectedOutput1, actualOutput1);

        // Test with null input
        var actualOutput2 = authenticationMapper.base64DecodingString(null);
        assertNull(actualOutput2);
    }

    @Test
    void testGetUUID() {
        var uuid = authenticationMapper.getUUID();
        assertNotNull(uuid);
        assertEquals(36, uuid.length());
        assertTrue(uuid.matches("[a-f0-9]{8}-[a-f0-9]{4}-[a-f0-9]{4}-[a-f0-9]{4}-[a-f0-9]{12}"));
    }

}