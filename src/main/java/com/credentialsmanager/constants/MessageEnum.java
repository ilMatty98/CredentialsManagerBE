package com.credentialsmanager.constants;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum MessageEnum {

    ERROR_00(0, "Generic error"),
    ERROR_01(1, "Email already registered"),
    ERROR_02(2, "Invalid credentials"),
    ERROR_03(3, "Invalid Token Jwt"),
    ERROR_04(-1, "Error sending email to %s"),
    ERROR_05(4, "User not found"),
    ERROR_06(5, "Unconfirmed email");

    private final int errorCode;
    private final String label;

    public String getMessage(Object... replacement) {
        if (replacement.length == 0) return label;
        return String.format(label, replacement);
    }
}
