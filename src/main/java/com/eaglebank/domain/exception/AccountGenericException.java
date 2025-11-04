package com.eaglebank.domain.exception;

import com.eaglebank.domain.model.account.AccountNumber;

/**
 * Exception thrown when an Account is not found
 */
public class AccountGenericException extends DomainException {

    public AccountGenericException(AccountNumber accountNumber) {
        super("Account error: " + accountNumber.getValue());
    }

    public AccountGenericException(String message) {
        super(message);
    }
}
