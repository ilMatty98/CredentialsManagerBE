package com.credentialsmanager.exception;

import com.credentialsmanager.utils.MessageUtils;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
public class CustomException extends RuntimeException {

    private final int codeMessage;

    public CustomException(MessageUtils messageUtils) {
        super(messageUtils.getMessage());
        this.codeMessage = messageUtils.getErrorCode();
    }
}
