package com.credentialsmanager.configuration.mapper;

import org.springframework.stereotype.Component;

@Component
public class AppMapperConverters {

    public String autoTrimField(String field) {
        return field != null ? field.trim() : null;
    }
}
