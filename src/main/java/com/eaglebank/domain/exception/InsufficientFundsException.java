package com.eaglebank.domain.exception;

import com.eaglebank.domain.model.account.AccountNumber;
import com.eaglebank.domain.model.account.Money;

/**
 * Exception thrown when an account has insufficient funds for a transaction
 */
public class InsufficientFundsException extends DomainException {

    public InsufficientFundsException(AccountNumber accountNumber, Money requestedAmount, Money availableBalance) {
        super("Insufficient funds in account " + accountNumber.getValue() +
                ". Requested: " + requestedAmount +
                ", Available: " + availableBalance);
    }

    public InsufficientFundsException(String message) {
        super(message);
    }
}
