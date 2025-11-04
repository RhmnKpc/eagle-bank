package com.eaglebank.domain.exception;

import com.eaglebank.domain.model.account.AccountNumber;

/**
 * Exception thrown when an Account is not found
 */
public class AccountNotFoundException extends DomainException {

    public AccountNotFoundException(AccountNumber accountNumber) {
        super("Account not found with account number: " + accountNumber.getValue());
    }

    public AccountNotFoundException(String message) {
        super(message);
    }
}
