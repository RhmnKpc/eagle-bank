package com.eaglebank.interfaces.rest.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

/**
 * Request DTO for creating a transaction
 */
public record CreateTransactionRequest(
        @Positive(message = "Amount must be positive")
        double amount,

        @NotBlank(message = "Currency is required")
        String currency,

        @NotBlank(message = "Transaction type is required")
        String type,

        String reference
) {
}
