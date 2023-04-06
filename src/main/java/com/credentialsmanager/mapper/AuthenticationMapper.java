package com.credentialsmanager.mapper;

import com.credentialsmanager.configuration.mapper.AppMapperConfig;
import com.credentialsmanager.dto.AuthenticationDto;
import com.credentialsmanager.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = AppMapperConfig.class)
public interface AuthenticationMapper {

    @Mapping(source = "salt", target = "salt")
    @Mapping(source = "hash", target = "hash")
    @Mapping(source = "authenticationDto.email", target = "email")
    User dtotoUser(AuthenticationDto authenticationDto, String salt, String hash);
}
