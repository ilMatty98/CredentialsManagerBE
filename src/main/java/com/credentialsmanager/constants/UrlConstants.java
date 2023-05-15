package com.credentialsmanager.constants;

import lombok.experimental.UtilityClass;

@UtilityClass
@SuppressWarnings("java:S1075")
public class UrlConstants {

    public static final String HEADER_EMAIL = "email";

    public static final String BASE_PATH = "/v1/authentication";
    public static final String SIGN_UP = "/signUp";
    public static final String LOG_IN = "/logIn";
    public static final String CHECK_EMAIL = "/checkEmail";
    public static final String CONFIRM_EMAIL = "/{email}/{code}/confirm";
    public static final String CHANGE_PASSWORD = "/changePassword";
    public static final String CHANGE_EMAIL = "/changeEmail";
    public static final String CHANGE_INFORMATION = "/changeInformation";
    public static final String SEND_HINT = "/sendHint";
}
