package com.credentialsmanager.mapper;

import com.credentialsmanager.configuration.mapper.AppMapperConfig;
import com.credentialsmanager.dto.AuthenticationDto;
import com.credentialsmanager.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.sql.Timestamp;
import java.util.Base64;

@Mapper(config = AppMapperConfig.class)
public interface AuthenticationMapper {

    @Mapping(target = "timestampCreation", source = "timestamp")
    @Mapping(target = "timestampLastAccess", source = "timestamp")
    @Mapping(target = "email", source = "authenticationDto.email")
    @Mapping(target = "salt", source = "salt", qualifiedByName = "base64Encoding")
    @Mapping(target = "payload", source = "payload", qualifiedByName = "base64Encoding")
    User saveNewUser(AuthenticationDto authenticationDto, byte[] salt, byte[] payload, Timestamp timestamp);

    @Named("base64Encoding")
    default String base64Encoding(byte[] input) {
        return Base64.getEncoder().encodeToString(input);
    }
}