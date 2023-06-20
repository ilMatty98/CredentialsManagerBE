package com.credentialsmanager.configuration.mapper;

import org.mapstruct.MapperConfig;

@MapperConfig(componentModel = "spring", uses = AppMapperConverters.class)
public interface AppMapperConfig {
}
