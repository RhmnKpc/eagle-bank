package com.eaglebank.interfaces.rest.dto.response;

import java.time.OffsetDateTime;

/**
 * Response DTO for Transaction
 */
public record TransactionResponse(
        String id,
        Double amount,
        String currency,
        String type,
        String reference,
        String userId,
        OffsetDateTime createdTimestamp
) {
}
