package com.eaglebank.domain.repository;

import com.eaglebank.domain.model.account.Account;
import com.eaglebank.domain.model.account.AccountNumber;
import com.eaglebank.domain.model.user.UserId;

import java.util.List;
import java.util.Optional;

/**
 * Account Repository Interface (Port)
 * <p>
 * Defines the contract for persisting and retrieving Account aggregates.
 * This is a port in the hexagonal architecture - implementations are adapters.
 */
public interface AccountRepository {

    /**
     * Saves an account (create or update)
     */
    Account save(Account account);

    /**
     * Finds an account by account number
     */
    Optional<Account> findByAccountNumber(AccountNumber accountNumber);

    /**
     * Finds all accounts owned by a specific user
     */
    List<Account> findByOwnerId(UserId ownerId);

    /**
     * Checks if an account exists by account number
     */
    boolean existsByAccountNumber(AccountNumber accountNumber);

    /**
     * Counts the number of accounts owned by a user
     */
    long countByOwnerId(UserId ownerId);

    /**
     * Deletes an account by account number
     */
    void deleteByAccountNumber(AccountNumber accountNumber);
}
