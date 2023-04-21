package com.credentialsmanager.constants;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum TokenClaimEnum {

    ROLE("role");

    private final String label;
}
