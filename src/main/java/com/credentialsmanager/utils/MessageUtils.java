package com.credentialsmanager.utils;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum MessageUtils {

    ERROR_00(0, "Generic error"),
    ERROR_01(1, "Email already registered"),
    ERROR_02(2, "Invalid credentials"),
    ERROR_03(3, "Invalid Token Jwt");

    private final int errorCode;
    private final String label;

    public String getMessage(Object... replacement) {
        if (replacement.length == 0) return label;
        return String.format(label, replacement);
    }
}
