package com.eaglebank.domain.model.transaction;

import lombok.Getter;

/**
 * Enum representing Transaction Types
 */
public enum TransactionType {
    DEPOSIT("Deposit", "Funds deposited into account"),
    WITHDRAWAL("Withdrawal", "Funds withdrawn from account");
    @Getter
    private final String displayName;
    @Getter
    private final String description;

    TransactionType(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }


    public boolean isCredit() {
        return this == DEPOSIT;
    }

    public boolean isDebit() {
        return this == WITHDRAWAL;
    }
}
