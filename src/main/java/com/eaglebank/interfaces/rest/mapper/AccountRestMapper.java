package com.eaglebank.interfaces.rest.mapper;

import com.eaglebank.domain.model.account.Account;
import com.eaglebank.interfaces.rest.dto.response.BankAccountResponse;
import org.springframework.stereotype.Component;

import java.time.ZoneOffset;

/**
 * Mapper between Account domain model and REST DTOs
 */
@Component
public class AccountRestMapper {

    public BankAccountResponse toResponse(Account account) {
        return new BankAccountResponse(
            account.getAccountNumber().getValue(),
            account.getSortCode().getValue(),
            account.getName(),
            account.getType().name().toLowerCase(),
            account.getBalance().getAmount().doubleValue(),
            account.getBalance().getCurrency().getCurrencyCode(),
            account.getCreatedAt().atOffset(ZoneOffset.UTC),
            account.getUpdatedAt().atOffset(ZoneOffset.UTC)
        );
    }
}
