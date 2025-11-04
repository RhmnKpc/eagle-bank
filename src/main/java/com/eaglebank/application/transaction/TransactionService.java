package com.eaglebank.application.transaction;

import com.eaglebank.domain.model.transaction.Transaction;

import java.util.List;

/**
 * Application-level service contract for working with {@link Transaction} objects.
 * <p>
 * Implementations orchestrate domain operations and enforce access control for:
 * <ul>
 *   <li>Creating a new transaction (deposit or withdrawal) for an account</li>
 *   <li>Fetching a single transaction by id</li>
 *   <li>Listing all transactions for an account</li>
 * </ul>
 * Note: Implementations are expected to validate account ownership for the provided
 * <code>requestingUserId</code> and to translate/propagate domain errors as runtime exceptions.
 * See {@link TransactionServiceImpl} for the default implementation.
 */
public interface TransactionService {

    /**
     * Creates and persists a transaction for the given account. Supported types typically include
     * {@code DEPOSIT} and {@code WITHDRAWAL} (case-insensitive).
     *
     * @param command immutable input carrying account number, requesting user, type, amount and reference
     * @return the created {@link Transaction}
     * @throws com.eaglebank.domain.exception.AccountNotFoundException    if the account does not exist
     * @throws com.eaglebank.domain.exception.UnauthorizedAccessException if the requesting user does not own the account
     * @throws IllegalArgumentException                                   if the transaction type is invalid
     * @throws UnsupportedOperationException                              if the transaction type is not supported yet
     */
    Transaction create(CreateTransactionCommand command);

    /**
     * Retrieves a specific transaction by its id for the given account, validating that the
     * requesting user owns the account and that the transaction belongs to that account.
     *
     * @param accountNumber    the bank account number the transaction should belong to
     * @param transactionId    the identifier of the transaction to fetch
     * @param requestingUserId the id of the user making the request (must own the account)
     * @return the matching {@link Transaction}
     * @throws com.eaglebank.domain.exception.AccountNotFoundException     if the account does not exist
     * @throws com.eaglebank.domain.exception.UnauthorizedAccessException  if the requesting user does not own the account
     * @throws com.eaglebank.domain.exception.TransactionNotFoundException if the transaction is not found or does not belong to the account
     */
    Transaction get(String accountNumber, String transactionId, String requestingUserId);

    /**
     * Lists all transactions for the given account, after validating the requesting user owns the account.
     *
     * @param accountNumber    the bank account number whose transactions will be listed
     * @param requestingUserId the id of the user making the request (must own the account)
     * @return a list of {@link Transaction} objects belonging to the account (possibly empty)
     * @throws com.eaglebank.domain.exception.AccountNotFoundException    if the account does not exist
     * @throws com.eaglebank.domain.exception.UnauthorizedAccessException if the requesting user does not own the account
     */
    List<Transaction> list(String accountNumber, String requestingUserId);

    /**
     * Immutable command used to create a new transaction.
     *
     * @param accountNumber    the account number the transaction will be applied to
     * @param requestingUserId the user id initiating the request (must own the account)
     * @param type             the transaction type, e.g. {@code DEPOSIT} or {@code WITHDRAWAL}
     * @param amount           the monetary amount of the transaction (in GBP, non-negative)
     * @param reference        a human-readable reference or memo associated with the transaction
     */
    record CreateTransactionCommand(
            String accountNumber,
            String requestingUserId,
            String type,
            double amount,
            String reference
    ) {
    }
}
