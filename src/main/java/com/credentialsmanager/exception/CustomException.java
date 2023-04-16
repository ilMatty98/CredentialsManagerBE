package com.credentialsmanager.exception;

import com.credentialsmanager.constants.MessageEnum;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
public class CustomException extends RuntimeException {

    private final int codeMessage;

    public CustomException(MessageEnum messageEnum) {
        super(messageEnum.getMessage());
        this.codeMessage = messageEnum.getErrorCode();
    }
}
