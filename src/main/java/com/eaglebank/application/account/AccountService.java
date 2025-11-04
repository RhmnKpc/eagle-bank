package com.eaglebank.application.account;

import com.eaglebank.domain.model.account.Account;

import java.util.List;

/**
 * Application service that exposes use cases for managing bank accounts
 * within the Eagle Bank system.
 *
 * <p>Responsibilities include creating, reading, updating and deleting
 * accounts while enforcing ownership/authorization rules delegated to
 * the domain and/or security layers.</p>
 *
 * <p>This is an application boundary; implementations should remain thin
 * and orchestrate domain services, repositories and mappers.</p>
 *
 * @since 1.0.0
 */
public interface AccountService {

    /**
     * Creates a new bank account for the specified user.
     *
     * @param command payload containing the owner user ID, the account display name
     *                and the desired account type (e.g. "personal").
     * @return the newly created {@link Account}
     * @throws IllegalArgumentException if the command is invalid (e.g. blank name or type)
     */
    Account create(CreateAccountCommand command);

    /**
     * Deletes an existing account.
     *
     * <p>Implementations should ensure the {@code requestingUserId} is authorized
     * to delete the account (i.e. matches the account owner) and handle any domain
     * constraints.</p>
     *
     * @param accountNumber    the account number to delete
     * @param requestingUserId the user performing the operation (must be the owner)
     * @throws com.eaglebank.domain.exception.AccountNotFoundException    if the account does not exist
     * @throws com.eaglebank.domain.exception.UnauthorizedAccessException if the requester is not the owner
     */
    void delete(String accountNumber, String requestingUserId);

    /**
     * Retrieves a single account ensuring the requester is authorized to view it.
     *
     * @param accountNumber    the account number to fetch
     * @param requestingUserId the requesting user ID; must own the account
     * @return the {@link Account}
     * @throws com.eaglebank.domain.exception.AccountNotFoundException    if the account does not exist
     * @throws com.eaglebank.domain.exception.UnauthorizedAccessException if the requester is not the owner
     */
    Account get(String accountNumber, String requestingUserId);

    /**
     * Lists all accounts owned by the given user.
     *
     * @param userId the owner user ID
     * @return list of {@link Account} belonging to the user; never {@code null}
     */
    List<Account> list(String userId);

    /**
     * Updates the mutable attributes of an account (e.g. display name).
     *
     * @param command payload containing account identifier, requester user ID and new values
     * @return the updated {@link Account}
     * @throws com.eaglebank.domain.exception.AccountNotFoundException    if the account does not exist
     * @throws com.eaglebank.domain.exception.UnauthorizedAccessException if the requester is not the owner
     * @throws IllegalArgumentException                                   if provided values are invalid
     */
    Account update(UpdateAccountCommand command);

    /**
     * Command for creating an account.
     *
     * @param userId      the owner user ID
     * @param accountName the display name of the account
     * @param accountType the type of the account (domain-supported values, e.g. "personal")
     */
    record CreateAccountCommand(
            String userId,
            String accountName,
            String accountType
    ) {
    }

    /**
     * Command for updating an existing account.
     *
     * @param accountNumber    the account number to update
     * @param requestingUserId the user performing the update; must be the owner
     * @param accountName      the new display name for the account
     */
    record UpdateAccountCommand(
            String accountNumber,
            String requestingUserId,
            String accountName
    ) {
    }
}
