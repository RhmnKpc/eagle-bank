package com.eaglebank.domain.model.account;

import com.eaglebank.domain.exception.AccountGenericException;
import com.eaglebank.domain.model.user.UserId;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Account Aggregate Root
 * <p>
 * Represents a bank account owned by a user.
 * This is an aggregate root that encapsulates account state and business rules.
 */
@Data
@NoArgsConstructor(force = true)
@EqualsAndHashCode
public class Account {
    private final AccountNumber accountNumber;
    private final SortCode sortCode;
    private final UserId ownerId;
    private String name;
    private AccountType type;
    private AccountStatus status;
    private Money balance;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private Account(AccountNumber accountNumber, SortCode sortCode, UserId ownerId,
                    String name, AccountType type) {
        if (accountNumber == null) {
            throw new IllegalArgumentException("Account number cannot be null");
        }
        if (sortCode == null) {
            throw new IllegalArgumentException("Sort code cannot be null");
        }
        if (ownerId == null) {
            throw new IllegalArgumentException("Owner ID cannot be null");
        }
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Account name cannot be null or empty");
        }
        if (type == null) {
            throw new IllegalArgumentException("Account type cannot be null");
        }

        this.accountNumber = accountNumber;
        this.sortCode = sortCode;
        this.ownerId = ownerId;
        this.name = name;
        this.type = type;
        this.status = AccountStatus.ACTIVE;
        this.balance = Money.zero();
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public static Account create(AccountNumber accountNumber, SortCode sortCode,
                                 UserId ownerId, String accountName, AccountType accountType) {
        return new Account(accountNumber, sortCode, ownerId, accountName, accountType);
    }

    public static Account reconstitute(AccountNumber accountNumber, SortCode sortCode,
                                       UserId ownerId, String accountName, AccountType accountType,
                                       AccountStatus status, Money balance,
                                       LocalDateTime createdAt, LocalDateTime updatedAt) {
        Account account = new Account(accountNumber, sortCode, ownerId, accountName, accountType);
        account.status = status;
        account.balance = balance;
        account.createdAt = createdAt;
        account.updatedAt = updatedAt;
        return account;
    }

    public void deposit(Money amount) {
        if (!status.canPerformTransactions()) {
            throw new AccountGenericException("Cannot deposit to account with status: " + status);
        }
        if (amount.isNegative() || amount.isZero()) {
            throw new AccountGenericException("Deposit amount must be positive");
        }
        this.balance = this.balance.add(amount);
        this.updatedAt = LocalDateTime.now();
    }

    public void withdraw(Money amount) {
        if (!status.canPerformTransactions()) {
            throw new AccountGenericException("Cannot withdraw from account with status: " + status);
        }
        if (amount.isNegative() || amount.isZero()) {
            throw new AccountGenericException("Withdrawal amount must be positive");
        }
        if (this.balance.isLessThan(amount)) {
            throw new AccountGenericException("Insufficient funds for withdrawal");
        }
        this.balance = this.balance.subtract(amount);
        this.updatedAt = LocalDateTime.now();
    }

    public void updateAccountName(String newName) {
        if (newName == null || newName.isBlank()) {
            throw new AccountGenericException("Account name cannot be null or empty");
        }
        this.name = newName;
        this.updatedAt = LocalDateTime.now();
    }

    public void close() {
        if (status == AccountStatus.CLOSED) {
            throw new AccountGenericException("Account is already closed");
        }
        if (!balance.isZero()) {
            throw new AccountGenericException("Cannot close account with non-zero balance");
        }
        this.status = AccountStatus.CLOSED;
        this.updatedAt = LocalDateTime.now();
    }

    public void suspend() {
        if (status == AccountStatus.CLOSED) {
            throw new AccountGenericException("Cannot suspend a closed account");
        }
        this.status = AccountStatus.SUSPENDED;
        this.updatedAt = LocalDateTime.now();
    }

    public void activate() {
        if (status == AccountStatus.CLOSED) {
            throw new AccountGenericException("Cannot activate a closed account");
        }
        this.status = AccountStatus.ACTIVE;
        this.updatedAt = LocalDateTime.now();
    }

    public boolean isOwnedBy(UserId userId) {
        return this.ownerId.equals(userId);
    }
}
