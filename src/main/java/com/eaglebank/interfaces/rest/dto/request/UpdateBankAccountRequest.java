package com.eaglebank.interfaces.rest.dto.request;

/**
 * Request DTO for updating a bank account
 */
public record UpdateBankAccountRequest(
        String name,
        String accountType
) {
}
