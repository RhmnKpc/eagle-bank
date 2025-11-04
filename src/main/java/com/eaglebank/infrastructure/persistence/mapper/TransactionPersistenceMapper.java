package com.eaglebank.infrastructure.persistence.mapper;

import com.eaglebank.domain.model.account.AccountNumber;
import com.eaglebank.domain.model.account.Money;
import com.eaglebank.domain.model.transaction.Transaction;
import com.eaglebank.domain.model.transaction.TransactionId;
import com.eaglebank.domain.model.transaction.TransactionReference;
import com.eaglebank.domain.model.transaction.TransactionType;
import com.eaglebank.infrastructure.persistence.entity.TransactionEntity;
import org.springframework.stereotype.Component;

import java.util.Currency;

/**
 * Mapper between Transaction domain model and TransactionEntity
 */
@Component
public class TransactionPersistenceMapper {

    public TransactionEntity toEntity(Transaction transaction) {
        TransactionEntity entity = new TransactionEntity();
        entity.setId(transaction.getId().getValue());
        entity.setAccountNumber(transaction.getAccountNumber().getValue());
        entity.setType(toEntityTransactionType(transaction.getType()));
        entity.setAmount(transaction.getAmount().getAmount());
        entity.setCurrency(transaction.getAmount().getCurrency().getCurrencyCode());
        entity.setBalanceAfter(transaction.getBalanceAfter().getAmount());
        entity.setReference(transaction.getReference().getValue());
        entity.setCreatedAt(transaction.getCreatedAt());
        return entity;
    }

    public Transaction toDomain(TransactionEntity entity) {
        Currency currency = Currency.getInstance(entity.getCurrency());

        return Transaction.reconstitute(
                TransactionId.of(entity.getId()),
                AccountNumber.of(entity.getAccountNumber()),
                toDomainTransactionType(entity.getType()),
                Money.of(entity.getAmount(), currency),
                Money.of(entity.getBalanceAfter(), currency),
                TransactionReference.of(entity.getReference()),
                entity.getCreatedAt()
        );
    }

    private TransactionEntity.TransactionTypeEntity toEntityTransactionType(TransactionType type) {
        return TransactionEntity.TransactionTypeEntity.valueOf(type.name());
    }

    private TransactionType toDomainTransactionType(TransactionEntity.TransactionTypeEntity type) {
        return TransactionType.valueOf(type.name());
    }
}
