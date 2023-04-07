package com.credentialsmanager.mapper;

import com.credentialsmanager.configuration.mapper.AppMapperConfig;
import com.credentialsmanager.dto.AuthenticationDto;
import com.credentialsmanager.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.Base64;

@Mapper(config = AppMapperConfig.class)
public interface AuthenticationMapper {

    @Mapping(source = "salt", target = "salt", qualifiedByName = "base64Encoding")
    @Mapping(source = "hash", target = "hash", qualifiedByName = "base64Encoding")
    @Mapping(source = "authenticationDto.email", target = "email")
    User dtotoUser(AuthenticationDto authenticationDto, byte[] salt, byte[] hash);

    @Named("base64Encoding")
    default String base64Encoding(byte[] input) {
        return Base64.getEncoder().encodeToString(input);
    }
}
