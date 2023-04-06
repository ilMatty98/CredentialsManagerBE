package com.credentialsmanager.configuration.mapper;

import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

@Component
public class AppMapperConverters {

    public String autoTrimField(String field) {
        return field != null ? field.trim() : null;
    }

    public byte[] stringToByte(String string) {
        return string != null ? string.getBytes(StandardCharsets.ISO_8859_1) : new byte[]{};
    }
}
