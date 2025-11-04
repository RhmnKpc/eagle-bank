package com.eaglebank.domain.model.user;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.Value;

import java.util.regex.Pattern;

/**
 * Value Object representing an Email address
 */
@Value
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class Email {
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
    );

    String value;

    private static String validateAndNormalize(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Email cannot be null or empty");
        }
        String normalized = value.toLowerCase();
        if (!EMAIL_PATTERN.matcher(normalized).matches()) {
            throw new IllegalArgumentException("Invalid email format: " + value);
        }
        return normalized;
    }

    public static Email of(String value) {
        return new Email(validateAndNormalize(value));
    }
}
