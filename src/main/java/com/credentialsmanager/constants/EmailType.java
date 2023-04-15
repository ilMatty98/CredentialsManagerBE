package com.credentialsmanager.constants;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum EmailType {

    SING_UP("signUp.html"),
    LOG_IN("logIn.html");

    private final String fileName;
}
