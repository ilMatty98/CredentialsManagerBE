package com.credentialsmanager.mapper;

import com.credentialsmanager.configuration.mapper.AppMapperConfig;
import com.credentialsmanager.constants.UserStateEnum;
import com.credentialsmanager.dto.LoginDto;
import com.credentialsmanager.dto.SignUpDto;
import com.credentialsmanager.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.sql.Timestamp;
import java.util.Base64;
import java.util.UUID;

@Mapper(config = AppMapperConfig.class)
public interface AuthenticationMapper {

    @Mapping(target = "state", source = "userStateEnum")
    @Mapping(target = "email", source = "signUpDto.email")
    @Mapping(target = "language", source = "signUpDto.language")
    @Mapping(target = "timestampCreation", source = "timestamp")
    @Mapping(target = "timestampLastAccess", source = "timestamp")
    @Mapping(target = "verificationCode", expression = "java(getUUID())")
    @Mapping(target = "salt", source = "salt", qualifiedByName = "base64Encoding")
    @Mapping(target = "hash", source = "hash", qualifiedByName = "base64Encoding")
    @Mapping(target = "initializationVector", source = "signUpDto.initializationVector", qualifiedByName = "base64EncodingString")
    @Mapping(target = "protectedSymmetricKey", source = "signUpDto.protectedSymmetricKey", qualifiedByName = "base64EncodingString")
    User newUser(SignUpDto signUpDto, byte[] salt, byte[] hash, Timestamp timestamp, UserStateEnum userStateEnum);

    @Mapping(target = "token", source = "token")
    @Mapping(target = "tokenPublicKey", source = "tokenPublicKey")
    @Mapping(target = "initializationVector", source = "user.initializationVector", qualifiedByName = "base64DecodingString")
    @Mapping(target = "protectedSymmetricKey", source = "user.protectedSymmetricKey", qualifiedByName = "base64DecodingString")
    LoginDto.Response newLoginDto(User user, String token, String tokenPublicKey);

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

    @Named("getUUID")
    default String getUUID() {
        return UUID.randomUUID().toString();
    }


}