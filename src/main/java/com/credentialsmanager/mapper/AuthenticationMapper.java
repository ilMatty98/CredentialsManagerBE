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
    @Mapping(target = "hash", source = "hash", qualifiedByName = "base64Encoding")
    @Mapping(target = "protectedSymmetricKey", source = "registrationDto.protectedSymmetricKey", qualifiedByName = "base64EncodingString")
    User newUser(RegistrationDto registrationDto, byte[] salt, byte[] hash, Timestamp timestamp);

    @Mapping(target = "token", source = "token")
    @Mapping(target = "protectedSymmetricKey", source = "user.protectedSymmetricKey", qualifiedByName = "base64DecodingString")
    LoginDto.Response newLoginDto(User user, String token);

    @Named("base64Encoding")
    default String base64Encoding(byte[] input) {
        return input != null ? Base64.getEncoder().encodeToString(input) : null;
    }

    @Named("base64Decoding")
    default byte[] base64Decoding(String input) {
        return input != null ? Base64.getDecoder().decode(input) : null;
    }

    @Named("base64EncodingString")
    default String base64EncodingString(String input) {
        return input != null ? base64Encoding(input.getBytes()) : null;
    }

    @Named("base64DecodingString")
    default String base64DecodingString(String input) {
        return input != null ? new String(base64Decoding(input)) : null;
    }


}