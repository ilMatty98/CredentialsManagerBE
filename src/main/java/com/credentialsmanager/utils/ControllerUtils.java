package com.credentialsmanager.utils;

import com.credentialsmanager.constants.MessageEnum;
import com.credentialsmanager.constants.TokenClaimEnum;
import com.credentialsmanager.exception.CustomException;
import com.credentialsmanager.exception.GenericErrorException;
import com.credentialsmanager.exception.UnauthorizedException;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import lombok.experimental.UtilityClass;

import java.util.Optional;
import java.util.function.Supplier;

@UtilityClass
public class ControllerUtils {

    public <T> T handleRequest(Supplier<T> supplier) {
        try {
            return supplier.get();
        } catch (CustomException customException) {
            throw customException;
        } catch (Exception e) {
            throw new GenericErrorException(e);
        }
    }

    public static String getEmailFromToken(HttpServletRequest request) {
        var claims = (Claims) request.getAttribute(TokenClaimEnum.CLAIMS.getLabel());
        return Optional.ofNullable(claims)
                .map(c -> c.get(TokenClaimEnum.EMAIL.getLabel()))
                .map(Object::toString)
                .orElseThrow(() -> new UnauthorizedException(MessageEnum.ERROR_02));
    }
}
