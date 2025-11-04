package com.eaglebank.domain.exception;

import com.eaglebank.domain.model.user.UserId;

/**
 * Exception thrown when attempting to delete a user who still has active accounts
 */
public class UserHasAccountsException extends DomainException {

    public UserHasAccountsException(UserId userId, long accountCount) {
        super("Cannot delete user " + userId.getValue() +
                " because they have " + accountCount + " active account(s). " +
                "Please close all accounts before deleting the user.");
    }

    public UserHasAccountsException(String message) {
        super(message);
    }
}
