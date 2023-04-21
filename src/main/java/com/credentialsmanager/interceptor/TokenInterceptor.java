package com.credentialsmanager.interceptor;

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

    private final TokenJwtService tokenJwtService;

    @Override
    public boolean preHandle(HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull Object handler) {
        var token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
            if (tokenJwtService.verifySignAndRole(token, UserStateEnum.VERIFIED.name())) {
                return true;
            }
        }
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        return false;
    }

}
