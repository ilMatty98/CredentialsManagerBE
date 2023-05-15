package com.credentialsmanager.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class ChangeHintDto extends ChangeDto {

    @NotBlank(message = "Hint cannot be blank")
    private String hint;

}
