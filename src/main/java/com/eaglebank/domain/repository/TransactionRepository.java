package com.eaglebank.domain.repository;

import com.eaglebank.domain.model.account.AccountNumber;
import com.eaglebank.domain.model.transaction.Transaction;
import com.eaglebank.domain.model.transaction.TransactionId;

import java.util.List;
import java.util.Optional;

/**
 * Transaction Repository Interface (Port)
 * <p>
 * Defines the contract for persisting and retrieving Transactions.
 * This is a port in the hexagonal architecture - implementations are adapters.
 */
public interface TransactionRepository {

    /**
     * Saves a transaction
     */
    Transaction save(Transaction transaction);

    /**
     * Finds a transaction by ID
     */
    Optional<Transaction> findById(TransactionId transactionId);

    /**
     * Finds all transactions for a specific account
     */
    List<Transaction> findByAccountNumber(AccountNumber accountNumber);

}
