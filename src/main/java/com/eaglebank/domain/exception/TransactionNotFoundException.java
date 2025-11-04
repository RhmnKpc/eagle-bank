package com.eaglebank.domain.exception;

import com.eaglebank.domain.model.transaction.TransactionId;

/**
 * Exception thrown when a Transaction is not found
 */
public class TransactionNotFoundException extends DomainException {

    public TransactionNotFoundException(TransactionId transactionId) {
        super("Transaction not found with ID: " + transactionId.getValue());
    }

    public TransactionNotFoundException(String message) {
        super(message);
    }
}
