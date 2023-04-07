package com.credentialsmanager.exception;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class UnauthorizedException extends CustomException {

    public UnauthorizedException(String message) {
        super(message);
        log.error(message);
    }
}
