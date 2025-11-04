package com.eaglebank.domain.model.account;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.Value;

/**
 * Value Object representing a Sort Code
 * Format: 10-10-10 (UK sort code format)
 */
@Value
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class SortCode {
    private static final String DEFAULT_SORT_CODE = "10-10-10";

    String value;

    private static void validate(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Sort code cannot be null or empty");
        }
        if (!value.matches("^\\d{2}-\\d{2}-\\d{2}$")) {
            throw new IllegalArgumentException("Invalid sort code format. Must be XX-XX-XX");
        }
    }

    public static SortCode of(String value) {
        validate(value);
        return new SortCode(value);
    }

    public static SortCode defaultSortCode() {
        return new SortCode(DEFAULT_SORT_CODE);
    }
}
