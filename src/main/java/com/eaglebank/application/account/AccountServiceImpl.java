package com.eaglebank.application.account;

import com.eaglebank.domain.exception.AccountGenericException;
import com.eaglebank.domain.exception.AccountNotFoundException;
import com.eaglebank.domain.exception.UnauthorizedAccessException;
import com.eaglebank.domain.exception.UserNotFoundException;
import com.eaglebank.domain.model.account.Account;
import com.eaglebank.domain.model.account.AccountNumber;
import com.eaglebank.domain.model.account.AccountType;
import com.eaglebank.domain.model.account.SortCode;
import com.eaglebank.domain.model.user.UserId;
import com.eaglebank.domain.repository.AccountRepository;
import com.eaglebank.domain.repository.UserRepository;
import com.eaglebank.domain.service.AccountDomainService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
@Transactional
@RequiredArgsConstructor
public class AccountServiceImpl implements
        AccountService {

    private final AccountRepository accountRepository;
    private final UserRepository userRepository;
    private final AccountDomainService accountDomainService;

    @Override
    public Account create(CreateAccountCommand command) {
        // Verify user exists
        UserId ownerId = UserId.of(command.userId());
        if (!userRepository.existsById(ownerId)) {
            throw new UserNotFoundException(ownerId);
        }

        // Parse account type
        AccountType accountType;
        try {
            accountType = AccountType.valueOf(command.accountType().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid account type: " + command.accountType());
        }

        // Generate unique account number
        AccountNumber accountNumber;
        do {
            accountNumber = AccountNumber.generate();
        } while (accountRepository.existsByAccountNumber(accountNumber));

        // Create account
        Account account = Account.create(
                accountNumber,
                SortCode.defaultSortCode(),
                ownerId,
                command.accountName(),
                accountType
        );

        return accountRepository.save(account);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Account> list(String userId) {
        UserId ownerId = UserId.of(userId);

        // Verify user exists
        if (!userRepository.existsById(ownerId)) {
            throw new UserNotFoundException(ownerId);
        }

        return accountRepository.findByOwnerId(ownerId);
    }

    @Override
    public Account get(String number, String requestingUserId) {
        AccountNumber accountNumber = AccountNumber.of(number);
        UserId userId = UserId.of(requestingUserId);

        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new AccountNotFoundException(accountNumber));

        if (!account.isOwnedBy(userId)) {
            throw new UnauthorizedAccessException(userId, accountNumber);
        }
        return account;

    }

    @Override
    public Account update(UpdateAccountCommand command) {
        AccountNumber accountNumber = AccountNumber.of(command.accountNumber());
        UserId userId = UserId.of(command.requestingUserId());

        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new AccountNotFoundException(accountNumber));

        // Verify ownership
        if (!account.isOwnedBy(userId)) {
            throw new UnauthorizedAccessException(userId, accountNumber);
        }

        // Update account name
        account.updateAccountName(command.accountName());

        return account;
    }

    @Override
    public void delete(String accountNumber, String requestingUserId) {
        AccountNumber accNum = AccountNumber.of(accountNumber);
        UserId userId = UserId.of(requestingUserId);

        Account account = accountRepository.findByAccountNumber(accNum)
                .orElseThrow(() -> new AccountNotFoundException(accNum));

        // Verify ownership
        if (!account.isOwnedBy(userId)) {
            throw new UnauthorizedAccessException(userId, accNum);
        }

        // Check if account can be closed
        if (!accountDomainService.canCloseAccount(account)) {
            throw new AccountGenericException(
                    "Account cannot be closed. Balance must be zero and account must be active."
            );
        }

        // Close and delete account
        account.close();
        accountRepository.deleteByAccountNumber(accNum);
    }


}
