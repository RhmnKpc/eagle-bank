package com.eaglebank.domain.exception;

import com.eaglebank.domain.model.account.AccountNumber;
import com.eaglebank.domain.model.user.UserId;

/**
 * Exception thrown when a user attempts to access a resource they don't own
 */
public class UnauthorizedAccessException extends DomainException {

    public UnauthorizedAccessException(UserId userId, AccountNumber accountNumber) {
        super("User " + userId.getValue() +
                " is not authorized to access account " + accountNumber.getValue());
    }

    public UnauthorizedAccessException(String message) {
        super(message);
    }
}
