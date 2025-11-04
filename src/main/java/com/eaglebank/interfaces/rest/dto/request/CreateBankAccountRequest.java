package com.eaglebank.interfaces.rest.dto.request;

import jakarta.validation.constraints.NotBlank;

/**
 * Request DTO for creating a bank account
 */
public record CreateBankAccountRequest(
        @NotBlank(message = "Name is required")
        String name,

        @NotBlank(message = "Account type is required")
        String accountType
) {
}
