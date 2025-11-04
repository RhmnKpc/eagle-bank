package com.eaglebank.domain.service;

import com.eaglebank.domain.model.account.Account;

/**
 * Account Domain Service
 * <p>
 * Contains complex business logic that doesn't naturally fit within
 * a single aggregate root.
 */
public class AccountDomainService {

    /**
     * Validates if an account can be closed
     */
    public boolean canCloseAccount(Account account) {
        return account.getBalance().isZero() &&
                account.getStatus().canPerformTransactions();
    }
}
