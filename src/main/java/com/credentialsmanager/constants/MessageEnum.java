package com.credentialsmanager.constants;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum MessageEnum {

    ERROR_00(0, "Generic error"),
    ERROR_01(1, "Email already registered"),
    ERROR_02(2, "Invalid credentials"),
    ERROR_03(3, "User not found"),
    ERROR_04(4, "Unconfirmed email"),
    ERROR_05(5, "The attempt limit has been reached"),
    ERROR_06(6, "The maximum time limit has been exceeded"),
    ERROR_07(7, "Incorrect verification code"),
    ERROR_08(8, "Invalid jwt token"),
    ERROR_99(-1, "Error sending email to %s");

    private final int errorCode;
    private final String label;

    public String getMessage(Object... replacement) {
        if (replacement.length == 0) return label;
        return String.format(label, replacement);
    }
}
