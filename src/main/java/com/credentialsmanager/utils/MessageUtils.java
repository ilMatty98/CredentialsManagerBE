package com.credentialsmanager.utils;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum MessageUtils {

    ERROR_01("Email already present");

    private final String label;

    public String getMessage(Object... replacement) {
        if (replacement.length == 0) return label;
        return String.format(label, replacement);
    }
}
