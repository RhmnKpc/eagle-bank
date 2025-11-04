package com.eaglebank.domain.model.transaction;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.Value;

import java.util.UUID;

/**
 * Value Object representing a Transaction's unique identifier
 * Format: tan-{uuid}
 */
@Value
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class TransactionId {
    private static final String PREFIX = "tan-";

    String value;

    private static void validate(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("TransactionId cannot be null or empty");
        }
    }

    public static TransactionId of(String value) {
        validate(value);
        return new TransactionId(value);
    }

    public static TransactionId generate() {
        return new TransactionId(PREFIX + UUID.randomUUID());
    }
}
