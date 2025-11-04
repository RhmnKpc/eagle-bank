package com.eaglebank.interfaces.rest.dto.response;

import java.util.List;

/**
 * Response DTO for listing transactions
 */
public record ListTransactionsResponse(
        List<TransactionResponse> transactions
) {
}
