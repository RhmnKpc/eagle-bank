package com.eaglebank.interfaces.rest.dto.response;

import java.time.OffsetDateTime;

/**
 * Response DTO for Bank Account
 */
public record BankAccountResponse(
        String accountNumber,
        String sortCode,
        String name,
        String accountType,
        Double balance,
        String currency,
        OffsetDateTime createdTimestamp,
        OffsetDateTime updatedTimestamp
) {
}
