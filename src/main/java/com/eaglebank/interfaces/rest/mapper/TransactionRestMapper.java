package com.eaglebank.interfaces.rest.mapper;

import com.eaglebank.domain.exception.AccountGenericException;
import com.eaglebank.domain.model.account.Account;
import com.eaglebank.domain.model.transaction.Transaction;
import com.eaglebank.domain.repository.AccountRepository;
import com.eaglebank.interfaces.rest.dto.response.TransactionResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.ZoneOffset;

/**
 * Mapper between Transaction domain model and REST DTOs
 */
@Component
@RequiredArgsConstructor
public class TransactionRestMapper {

    private final AccountRepository accountRepository;

    public TransactionResponse toResponse(Transaction transaction) {
        // Get the account to retrieve the owner's user ID
        Account account = accountRepository.findByAccountNumber(transaction.getAccountNumber())
                .orElseThrow(() -> new AccountGenericException("Account not found for transaction"));

        return new TransactionResponse(
                transaction.getId().getValue(),
                transaction.getAmount().getAmount().doubleValue(),
                transaction.getAmount().getCurrency().getCurrencyCode(),
                transaction.getType().name().toLowerCase(),
                transaction.getReference().getValue(),
                account.getOwnerId().getValue(),
                transaction.getCreatedAt().atOffset(ZoneOffset.UTC)
        );
    }
}
