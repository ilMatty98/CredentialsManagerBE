package com.credentialsmanager.test.service;

import com.credentialsmanager.constants.MessageEnum;
import com.credentialsmanager.exception.UnauthorizedException;
import com.credentialsmanager.test.ApiTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockHttpServletRequest;

import java.util.Date;
import java.util.HashMap;

import static org.junit.Assert.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

class TokenJwtServiceTest extends ApiTest {

    private MockHttpServletRequest request;

    @BeforeEach
    void setUp() {
        request = new MockHttpServletRequest();
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGenerateTokenJwt() {
        var subjetc = "Subject";
        var claims = new HashMap<String, Object>();
        claims.put("claim1", generateRandomString(10));
        claims.put("claim2", generateRandomString(100));

        var token = tokenJwtService.generateTokenJwt(subjetc, claims);
        assertNotNull(token);

        var body = tokenJwtService.getBody(token);
        assertEquals(subjetc, body.getSubject());
        assertEquals(claims.get("claim1"), body.get("claim1"));
        assertEquals(claims.get("claim2"), body.get("claim2"));

        var date = new Date(new Date().getTime() + (tokenExpiration * 60 * 1000));
        assertTrue(date.after(body.getExpiration()));
    }

    @Test
    void testExpiration() throws NoSuchFieldException, IllegalAccessException {
        MockitoAnnotations.openMocks(tokenJwtService);
        var field = tokenJwtService.getClass().getDeclaredField("tokenExpiration");
        field.setAccessible(true);
        field.set(tokenJwtService, -tokenExpiration);

        var token = tokenJwtService.generateTokenJwt("subject", new HashMap<>());
        assertNotNull(token);

        var body = tokenJwtService.getBody(token);
        assertTrue(body.isEmpty());
        field.set(tokenJwtService, tokenExpiration);
    }

    @Test
    void testWithoutAuthorizationHeaders() {
        var exception = assertThrows(UnauthorizedException.class, () -> tokenJwtService.getEmailFromToken(request));
        assertEquals(MessageEnum.ERROR_08.getMessage(), exception.getMessage());
        assertEquals(MessageEnum.ERROR_08.getErrorCode(), exception.getCodeMessage());
    }

    @Test
    void testTokenNotStartsWithBearer() {
        request.addHeader("Authorization", "FakeBearer ");
        var exception = assertThrows(UnauthorizedException.class, () -> tokenJwtService.getEmailFromToken(request));
        assertEquals(MessageEnum.ERROR_08.getMessage(), exception.getMessage());
        assertEquals(MessageEnum.ERROR_08.getErrorCode(), exception.getCodeMessage());
    }

    @Test
    void testCheckAuthorization() throws Exception {
        signUp(EMAIL, PASSWORD);
        confirmEmail(EMAIL);
        request.addHeader("Authorization", "Bearer " + getTokenFromLogIn(EMAIL, PASSWORD));

        assertEquals(EMAIL, tokenJwtService.getEmailFromToken(request));
    }

}
