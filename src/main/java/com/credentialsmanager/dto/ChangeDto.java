package com.credentialsmanager.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

@Data
public abstract class ChangeDto {

    @JsonIgnore
    private String email;
}
