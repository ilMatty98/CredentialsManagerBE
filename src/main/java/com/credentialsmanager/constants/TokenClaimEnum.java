package com.credentialsmanager.constants;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum TokenClaimEnum {

    CLAIMS("claims"),
    EMAIL("email"),
    ROLE("role");

    private final String label;
}
