package com.credentialsmanager.test.interceptor;

import com.credentialsmanager.constants.TokenClaimEnum;
import com.credentialsmanager.constants.UserStateEnum;
import com.credentialsmanager.interceptor.TokenInterceptor;
import com.credentialsmanager.service.TokenJwtService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.method.HandlerMethod;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class TokenInterceptorTest {

    @Mock
    private TokenJwtService tokenJwtService;

    @InjectMocks
    private TokenInterceptor tokenInterceptor;

    private HandlerMethod handlerMethod;
    private MockHttpServletRequest request;
    private MockHttpServletResponse response;

    private static final String TOKEN = "token";

    @BeforeEach
    void setUp() throws NoSuchMethodException {
        MockitoAnnotations.openMocks(this);
        handlerMethod = new HandlerMethod(new TestController(), "TestMethod");
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
    }

    @Test
    void testWithoutAuthorizationHeaders() {
        assertFalse(tokenInterceptor.preHandle(request, response, handlerMethod));
        assertEquals(HttpStatus.UNAUTHORIZED.value(), response.getStatus());
    }

    @Test
    void testTokenNotStartsWithBearer() {
        request.addHeader("Authorization", "FakeBearer ");
        assertFalse(tokenInterceptor.preHandle(request, response, handlerMethod));
        assertEquals(HttpStatus.UNAUTHORIZED.value(), response.getStatus());
    }

    @Test
    void testClaimNull() {
        request.addHeader("Authorization", "Bearer " + TOKEN);
        when(tokenJwtService.getBody(TOKEN)).thenReturn(null);

        assertFalse(tokenInterceptor.preHandle(request, response, handlerMethod));
        assertEquals(HttpStatus.UNAUTHORIZED.value(), response.getStatus());
    }

    @Test
    void testClaimEmpty() {
        request.addHeader("Authorization", "Bearer " + TOKEN);
        when(tokenJwtService.getBody(TOKEN)).thenReturn(Jwts.claims());

        assertFalse(tokenInterceptor.preHandle(request, response, handlerMethod));
        assertEquals(HttpStatus.UNAUTHORIZED.value(), response.getStatus());
    }

    @Test
    void testClaimRoleUnverified() {
        request.addHeader("Authorization", "Bearer " + TOKEN);
        var claims = createClaims(TokenClaimEnum.ROLE.getLabel(), UserStateEnum.UNVERIFIED.name());
        when(tokenJwtService.getBody(TOKEN)).thenReturn(claims);

        assertFalse(tokenInterceptor.preHandle(request, response, handlerMethod));
        assertEquals(HttpStatus.UNAUTHORIZED.value(), response.getStatus());
    }

    @Test
    void testClaimRoleVerified() {
        request.addHeader("Authorization", "Bearer " + TOKEN);
        var claims = createClaims(TokenClaimEnum.ROLE.getLabel(), UserStateEnum.VERIFIED.name());
        when(tokenJwtService.getBody(TOKEN)).thenReturn(claims);

        assertTrue(tokenInterceptor.preHandle(request, response, handlerMethod));
        assertEquals(claims, request.getAttribute(TokenClaimEnum.CLAIMS.getLabel()));
    }

    private Claims createClaims(String key, String value) {
        var claim = Jwts.claims();
        if (key != null && value != null) claim.put(key, value);
        return claim;
    }

    private static class TestController {
        public void TestMethod() {
        }
    }
}
