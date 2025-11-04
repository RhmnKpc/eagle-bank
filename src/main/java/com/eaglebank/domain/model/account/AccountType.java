package com.eaglebank.domain.model.account;

import lombok.Getter;

/**
 * Enum representing Account Types
 */
public enum AccountType {
    PERSONAL("Personal Account"),
    BUSINESS("Business Account");
    @Getter
    private final String displayName;

    AccountType(String displayName) {
        this.displayName = displayName;
    }
}
