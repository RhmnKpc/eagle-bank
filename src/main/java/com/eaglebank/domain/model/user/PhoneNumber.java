package com.eaglebank.domain.model.user;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.Value;

import java.util.regex.Pattern;

/**
 * Value Object representing a Phone Number
 */
@Value
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class PhoneNumber {
    private static final Pattern PHONE_PATTERN = Pattern.compile(
            "^\\+?[1-9]\\d{1,14}$"  // E.164 format
    );

    String value;

    private static String validateAndNormalize(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Phone number cannot be null or empty");
        }
        String normalized = value.replaceAll("[\\s()-]", "");
        if (!PHONE_PATTERN.matcher(normalized).matches()) {
            throw new IllegalArgumentException("Invalid phone number format: " + value);
        }
        return normalized;
    }

    public static PhoneNumber of(String value) {
        return new PhoneNumber(validateAndNormalize(value));
    }
}
