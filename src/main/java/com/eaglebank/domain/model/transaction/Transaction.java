package com.eaglebank.domain.model.transaction;

import com.eaglebank.domain.model.account.AccountNumber;
import com.eaglebank.domain.model.account.Money;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * Transaction Entity (Immutable)
 * <p>
 * Represents a financial transaction on an account.
 * Transactions are immutable - once created, they cannot be modified.
 */
@Getter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Transaction {
    @EqualsAndHashCode.Include
    private final TransactionId id;
    private final AccountNumber accountNumber;
    private final TransactionType type;
    private final Money amount;
    private final Money balanceAfter;
    private final TransactionReference reference;
    private LocalDateTime createdAt;

    private Transaction(TransactionId id, AccountNumber accountNumber,
                        TransactionType type, Money amount, Money balanceAfter,
                        TransactionReference reference) {
        if (id == null) {
            throw new IllegalArgumentException("Transaction ID cannot be null");
        }
        if (accountNumber == null) {
            throw new IllegalArgumentException("Account number cannot be null");
        }
        if (type == null) {
            throw new IllegalArgumentException("Transaction type cannot be null");
        }
        if (amount == null) {
            throw new IllegalArgumentException("Amount cannot be null");
        }
        if (!amount.isPositive()) {
            throw new IllegalArgumentException("Transaction amount must be positive");
        }
        if (balanceAfter == null) {
            throw new IllegalArgumentException("Balance after cannot be null");
        }
        if (reference == null) {
            throw new IllegalArgumentException("Transaction reference cannot be null");
        }

        this.id = id;
        this.accountNumber = accountNumber;
        this.type = type;
        this.amount = amount;
        this.balanceAfter = balanceAfter;
        this.reference = reference;
        this.createdAt = LocalDateTime.now();
    }

    public static Transaction create(TransactionId id, AccountNumber accountNumber,
                                     TransactionType type, Money amount, Money balanceAfter,
                                     TransactionReference reference) {
        return new Transaction(id, accountNumber, type, amount, balanceAfter, reference);
    }

    public static Transaction reconstitute(TransactionId id, AccountNumber accountNumber,
                                           TransactionType type, Money amount, Money balanceAfter,
                                           TransactionReference reference,
                                           LocalDateTime createdAt) {
        Transaction transaction = new Transaction(id, accountNumber, type, amount, balanceAfter,
                reference);
        transaction.createdAt = createdAt;
        return transaction;
    }

    public boolean isCredit() {
        return type.isCredit();
    }

    public boolean isDebit() {
        return type.isDebit();
    }
}
