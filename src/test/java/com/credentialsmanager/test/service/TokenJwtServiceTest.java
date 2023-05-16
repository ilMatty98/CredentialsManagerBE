package com.credentialsmanager.test.service;

import com.credentialsmanager.test.ApiTest;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;

import java.util.Date;
import java.util.HashMap;

import static org.junit.Assert.*;

class TokenJwtServiceTest extends ApiTest {

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

}
