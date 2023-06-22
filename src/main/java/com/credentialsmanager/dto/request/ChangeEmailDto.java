package com.credentialsmanager.dto.request;

import com.credentialsmanager.dto.BaseDto;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;


@Data
@EqualsAndHashCode(callSuper = true)
public class ChangeEmailDto extends BaseDto {

    @NotBlank(message = "MasterPasswordHash cannot be blank")
    private String masterPasswordHash;

}
