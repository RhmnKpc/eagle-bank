package com.eaglebank.domain.model.transaction;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.Value;

/**
 * Value Object representing a Transaction Reference
 */
@Value
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class TransactionReference {
    String value;

    private static void validate(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Transaction reference cannot be null or empty");
        }
        if (value.length() > 100) {
            throw new IllegalArgumentException("Transaction reference cannot exceed 100 characters");
        }
    }

    public static TransactionReference of(String value) {
        validate(value);
        return new TransactionReference(value);
    }
}
