package com.credentialsmanager.interceptor;

import com.credentialsmanager.constants.TokenClaimEnum;
import com.credentialsmanager.constants.UserStateEnum;
import com.credentialsmanager.service.TokenJwtService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.servlet.HandlerInterceptor;

@RequiredArgsConstructor
public class TokenInterceptor implements HandlerInterceptor {

    private static final String AUTH_HEADER_NAME = "Authorization";
    private static final String AUTH_HEADER_PREFIX = "Bearer ";


    private final TokenJwtService tokenJwtService;

    @Override
    public boolean preHandle(HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull Object handler) {
        var token = request.getHeader(AUTH_HEADER_NAME);
        if (token != null && token.startsWith(AUTH_HEADER_PREFIX)) {
            token = token.substring(7);
            var claims = tokenJwtService.getClaims(token);
            if (claims.get(TokenClaimEnum.ROLE.getLabel()).equals(UserStateEnum.VERIFIED.name())) {
                request.setAttribute(TokenClaimEnum.CLAIMS.getLabel(), claims);
                return true;
            }
        }
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        return false;
    }

}
