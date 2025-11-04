package com.eaglebank.application.transaction;

import com.eaglebank.domain.exception.AccountNotFoundException;
import com.eaglebank.domain.exception.TransactionNotFoundException;
import com.eaglebank.domain.exception.UnauthorizedAccessException;
import com.eaglebank.domain.model.account.Account;
import com.eaglebank.domain.model.account.AccountNumber;
import com.eaglebank.domain.model.account.Money;
import com.eaglebank.domain.model.transaction.Transaction;
import com.eaglebank.domain.model.transaction.TransactionId;
import com.eaglebank.domain.model.transaction.TransactionReference;
import com.eaglebank.domain.model.transaction.TransactionType;
import com.eaglebank.domain.model.user.UserId;
import com.eaglebank.domain.repository.AccountRepository;
import com.eaglebank.domain.repository.TransactionRepository;
import com.eaglebank.domain.service.TransactionDomainService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
@Transactional
@RequiredArgsConstructor
public class TransactionServiceImpl implements
        TransactionService {

    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;
    private final TransactionDomainService transactionDomainService;

    @Override
    public Transaction create(CreateTransactionCommand command) {
        AccountNumber accountNumber = AccountNumber.of(command.accountNumber());
        UserId userId = UserId.of(command.requestingUserId());

        // Get account
        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new AccountNotFoundException(accountNumber));

        // Verify ownership
        if (!account.isOwnedBy(userId)) {
            throw new UnauthorizedAccessException(userId, accountNumber);
        }

        // Parse transaction type
        TransactionType transactionType;
        try {
            transactionType = TransactionType.valueOf(command.type().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid transaction type: " + command.type());
        }

        Money amount = Money.gbp(command.amount());

        TransactionReference reference = TransactionReference.of(command.reference());

        Transaction transaction;
        if (transactionType == TransactionType.DEPOSIT) {
            transaction = transactionDomainService.createDepositTransaction(
                    account, amount, reference
            );
            account.deposit(amount);
        } else if (transactionType == TransactionType.WITHDRAWAL) {
            transaction = transactionDomainService.createWithdrawalTransaction(
                    account, amount, reference
            );
            account.withdraw(amount);
        } else {
            throw new UnsupportedOperationException(
                    "Transaction type " + transactionType + " is not yet supported"
            );
        }
        accountRepository.save(account);

        return transactionRepository.save(transaction);
    }

    @Override
    @Transactional(readOnly = true)
    public Transaction get(String accountNumber, String transactionId, String requestingUserId) {
        AccountNumber accNum = AccountNumber.of(accountNumber);
        TransactionId txnId = TransactionId.of(transactionId);
        UserId userId = UserId.of(requestingUserId);

        // Get account to verify ownership
        Account account = accountRepository.findByAccountNumber(accNum)
                .orElseThrow(() -> new AccountNotFoundException(accNum));

        // Verify ownership
        if (!account.isOwnedBy(userId)) {
            throw new UnauthorizedAccessException(userId, accNum);
        }

        // Get transaction
        Transaction transaction = transactionRepository.findById(txnId)
                .orElseThrow(() -> new TransactionNotFoundException(txnId));

        // Verify transaction belongs to this account
        if (!transaction.getAccountNumber().equals(accNum)) {
            throw new TransactionNotFoundException(
                    "Transaction " + txnId.getValue() + " does not belong to account " + accNum.getValue()
            );
        }

        return transaction;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Transaction> list(String accountNumber, String requestingUserId) {
        AccountNumber accNum = AccountNumber.of(accountNumber);
        UserId userId = UserId.of(requestingUserId);

        // Get account to verify ownership
        Account account = accountRepository.findByAccountNumber(accNum)
                .orElseThrow(() -> new AccountNotFoundException(accNum));

        if (!account.isOwnedBy(userId)) {
            throw new UnauthorizedAccessException(userId, accNum);
        }

        return transactionRepository.findByAccountNumber(accNum);
    }
}
