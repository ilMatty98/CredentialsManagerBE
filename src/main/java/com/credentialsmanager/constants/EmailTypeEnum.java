package com.credentialsmanager.constants;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.UtilityClass;


@Getter
@AllArgsConstructor
public enum EmailTypeEnum {

    LOG_IN(EmailConstants.BASE_PATH_LABEL + "logIn.json", EmailConstants.BASE_PATH_TEMPLATE + "logIn.html"),
    SING_UP(EmailConstants.BASE_PATH_LABEL + "signUp.json", EmailConstants.BASE_PATH_TEMPLATE + "signUp.html"),
    SEND_HINT(EmailConstants.BASE_PATH_LABEL + "sendHint.json", EmailConstants.BASE_PATH_TEMPLATE + "sendHint.html"),
    CHANGE_PSW(EmailConstants.BASE_PATH_LABEL + "changePSW.json", EmailConstants.BASE_PATH_TEMPLATE + "changePSW.html"),
    CHANGE_EMAIL(EmailConstants.BASE_PATH_LABEL + "changeEmail.json", EmailConstants.BASE_PATH_TEMPLATE + "changeEmail.html");

    private final String labelLocation;
    private final String templateLocation;

    @UtilityClass
    public static class EmailConstants {

        private static final String BASE_PATH = "src/main/resources/email/";
        private static final String BASE_PATH_LABEL = BASE_PATH + "label/";
        private static final String BASE_PATH_TEMPLATE = BASE_PATH + "template/";

        public static final String DEFAULT_LANGUAGE = "EN";
    }
}
