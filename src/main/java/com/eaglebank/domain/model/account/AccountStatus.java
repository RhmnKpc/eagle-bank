package com.eaglebank.domain.model.account;

import lombok.Getter;

/**
 * Enum representing Account Status
 */
public enum AccountStatus {
    ACTIVE("Active"),
    SUSPENDED("Suspended"),
    CLOSED("Closed");
    @Getter
    private final String displayName;

    AccountStatus(String displayName) {
        this.displayName = displayName;
    }

    public boolean canPerformTransactions() {
        return this == ACTIVE;
    }
}
