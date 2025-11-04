package com.eaglebank.domain.model.account;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.Value;

import java.util.Random;

/**
 * Value Object representing an Account Number
 * Format: 01xxxxxxx (8 digits starting with 01)
 */
@Value
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class AccountNumber {
    private static final String PREFIX = "01";
    private static final int TOTAL_LENGTH = 8;
    private static final Random RANDOM = new Random();

    String value;

    private static void validate(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Account number cannot be null or empty");
        }
        if (!value.matches("^01\\d{6}$")) {
            throw new IllegalArgumentException("Invalid account number format. Must be 01xxxxxx");
        }
    }

    public static AccountNumber of(String value) {
        validate(value);
        return new AccountNumber(value);
    }

    public static AccountNumber generate() {
        int randomPart = RANDOM.nextInt(1000000); // 0 to 999999
        String accountNumber = PREFIX + String.format("%06d", randomPart);
        return new AccountNumber(accountNumber);
    }
}
