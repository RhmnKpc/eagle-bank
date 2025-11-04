package com.eaglebank.domain.service;

import com.eaglebank.domain.exception.InsufficientFundsException;
import com.eaglebank.domain.model.account.Account;
import com.eaglebank.domain.model.account.Money;
import com.eaglebank.domain.model.transaction.Transaction;
import com.eaglebank.domain.model.transaction.TransactionId;
import com.eaglebank.domain.model.transaction.TransactionReference;
import com.eaglebank.domain.model.transaction.TransactionType;

/**
 * Transaction Domain Service
 * <p>
 * Contains business logic for creating and validating transactions.
 */
public class TransactionDomainService {

    /**
     * Creates a deposit transaction
     */
    public Transaction createDepositTransaction(Account account, Money amount,
                                                TransactionReference reference) {
        validateTransactionAmount(amount);

        Money newBalance = account.getBalance().add(amount);

        return Transaction.create(
                TransactionId.generate(),
                account.getAccountNumber(),
                TransactionType.DEPOSIT,
                amount,
                newBalance,
                reference);
    }

    /**
     * Creates a withdrawal transaction
     */
    public Transaction createWithdrawalTransaction(Account account, Money amount,
                                                   TransactionReference reference) {
        validateTransactionAmount(amount);

        if (account.getBalance().isLessThan(amount)) {
            throw new InsufficientFundsException(account.getAccountNumber(), amount, account.getBalance());
        }

        Money newBalance = account.getBalance().subtract(amount);

        return Transaction.create(
                TransactionId.generate(),
                account.getAccountNumber(),
                TransactionType.WITHDRAWAL,
                amount,
                newBalance,
                reference
        );
    }

    /**
     * Validates transaction amount
     */
    private void validateTransactionAmount(Money amount) {
        if (amount == null) {
            throw new IllegalArgumentException("Transaction amount cannot be null");
        }
        if (!amount.isPositive()) {
            throw new IllegalArgumentException("Transaction amount must be positive");
        }
    }
}
