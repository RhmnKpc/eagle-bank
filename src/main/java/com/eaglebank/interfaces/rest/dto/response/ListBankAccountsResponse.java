package com.eaglebank.interfaces.rest.dto.response;

import java.util.List;

/**
 * Response DTO for listing bank accounts
 */
public record ListBankAccountsResponse(
        List<BankAccountResponse> accounts
) {
}
