package com.eaglebank.domain.exception;

import com.eaglebank.domain.model.user.Email;
import com.eaglebank.domain.model.user.UserId;

/**
 * Exception thrown when a User is not found
 */
public class UserNotFoundException extends DomainException {

    public UserNotFoundException(UserId userId) {
        super("User not found with ID: " + userId.getValue());
    }

    public UserNotFoundException(Email email) {
        super("User not found with email: " + email.getValue());
    }

    public UserNotFoundException(String message) {
        super(message);
    }
}
