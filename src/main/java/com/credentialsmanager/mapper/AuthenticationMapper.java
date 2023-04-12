package com.credentialsmanager.mapper;

import com.credentialsmanager.configuration.mapper.AppMapperConfig;
import com.credentialsmanager.dto.LoginDto;
import com.credentialsmanager.dto.RegistrationDto;
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
    @Mapping(target = "email", source = "registrationDto.email")
    @Mapping(target = "salt", source = "salt", qualifiedByName = "base64Encoding")
    @Mapping(target = "payload", source = "payload", qualifiedByName = "base64Encoding")
    @Mapping(target = "protectedSymmetricKey", source = "registrationDto.protectedSymmetricKey")
    User newUser(RegistrationDto registrationDto, byte[] salt, byte[] payload, Timestamp timestamp);

    @Mapping(target = "token", source = "token")
    @Mapping(target = "protectedSymmetricKey", source = "protectedSymmetricKey")
    LoginDto.Response newLoginDto(String protectedSymmetricKey, String token);

    @Named("base64Encoding")
    default String base64Encoding(byte[] input) {
        return Base64.getEncoder().encodeToString(input);
    }

    @Named("base64Decoding")
    default byte[] base64Decoding(String input) {
        return Base64.getDecoder().decode(input);
    }

}