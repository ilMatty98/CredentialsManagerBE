package com.credentialsmanager.exception;

import com.credentialsmanager.constants.MessageEnum;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class UnauthorizedException extends CustomException {

    public UnauthorizedException(MessageEnum messageEnum) {
        super(messageEnum);
        log.error(messageEnum.getMessage());
    }
}
