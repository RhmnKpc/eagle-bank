package com.eaglebank.domain.model.user;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.Value;

import java.util.UUID;

/**
 * Value Object representing a User's unique identifier
 */
@Value
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class UserId {
    String value;

    private static void validate(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("UserId cannot be null or empty");
        }
    }

    public static UserId of(String value) {
        validate(value);
        return new UserId(value);
    }

    public static UserId generate() {
        return new UserId(UUID.randomUUID().toString());
    }
}
