package com.eaglebank.application.service;

import com.eaglebank.application.account.AccountService;
import com.eaglebank.application.account.AccountServiceImpl;
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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AccountServiceImplTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private AccountDomainService accountDomainService;

    @InjectMocks
    private AccountServiceImpl accountService;

    private UserId userId;
    private String accountName;

    @BeforeEach
    void setUp() {
        userId = UserId.of("usr-123");
        accountName = "Test Account";
    }

    @Test
    void shouldCreateAccount() {
        // given
        when(userRepository.existsById(userId)).thenReturn(true);
        when(accountRepository.existsByAccountNumber(any(AccountNumber.class))).thenReturn(false);
        when(accountRepository.save(any(Account.class))).thenAnswer(i -> i.getArgument(0));

        AccountService.CreateAccountCommand command = new AccountService.CreateAccountCommand(
                userId.getValue(),
                accountName,
                "PERSONAL"
        );

        // when
        Account result = accountService.create(command);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo(accountName);
        assertThat(result.getType()).isEqualTo(AccountType.PERSONAL);
        assertThat(result.getOwnerId()).isEqualTo(userId);
        verify(accountRepository).save(any(Account.class));
    }

    @Test
    void shouldThrowExceptionWhenCreatingAccountForNonExistentUser() {
        // given
        when(userRepository.existsById(userId)).thenReturn(false);

        AccountService.CreateAccountCommand command = new AccountService.CreateAccountCommand(
                userId.getValue(),
                accountName,
                "PERSONAL"
        );

        // when & then
        assertThatThrownBy(() -> accountService.create(command))
                .isInstanceOf(UserNotFoundException.class);

        verify(accountRepository, never()).save(any(Account.class));
    }

    @Test
    void shouldListAccountsForUser() {
        // given
        when(userRepository.existsById(userId)).thenReturn(true);

        Account account1 = Account.create(
                AccountNumber.of("01123456"),
                SortCode.defaultSortCode(),
                userId,
                "Account 1",
                AccountType.PERSONAL
        );

        Account account2 = Account.create(
                AccountNumber.of("01789012"),
                SortCode.defaultSortCode(),
                userId,
                "Account 2",
                AccountType.PERSONAL
        );

        when(accountRepository.findByOwnerId(userId)).thenReturn(List.of(account1, account2));

        // when
        List<Account> result = accountService.list(userId.getValue());

        // then
        assertThat(result).hasSize(2);
        assertThat(result).containsExactly(account1, account2);
    }

    @Test
    void shouldUpdateAccountName() {
        // given
        AccountNumber accountNumber = AccountNumber.of("01123456");
        Account account = Account.create(
                accountNumber,
                SortCode.defaultSortCode(),
                userId,
                "Old Name",
                AccountType.PERSONAL
        );

        when(accountRepository.findByAccountNumber(accountNumber)).thenReturn(Optional.of(account));

        AccountService.UpdateAccountCommand command = new AccountService.UpdateAccountCommand(
                accountNumber.getValue(),
                userId.getValue(),
                "New Name"
        );

        // when
        Account result = accountService.update(command);

        // then
        assertThat(result.getName()).isEqualTo("New Name");
    }

    @Test
    void shouldThrowExceptionWhenUpdatingAccountOwnedByDifferentUser() {
        // given
        AccountNumber accountNumber = AccountNumber.of("01123456");
        UserId differentUserId = UserId.of("usr-456");

        Account account = Account.create(
                accountNumber,
                SortCode.defaultSortCode(),
                differentUserId,
                "Account Name",
                AccountType.PERSONAL
        );

        when(accountRepository.findByAccountNumber(accountNumber)).thenReturn(Optional.of(account));

        AccountService.UpdateAccountCommand command = new AccountService.UpdateAccountCommand(
                accountNumber.getValue(),
                userId.getValue(),
                "New Name"
        );

        // when & then
        assertThatThrownBy(() -> accountService.update(command))
                .isInstanceOf(UnauthorizedAccessException.class);

        verify(accountRepository, never()).save(any(Account.class));
    }

    @Test
    void shouldDeleteAccount() {
        // given
        AccountNumber accountNumber = AccountNumber.of("01123456");
        Account account = Account.create(
                accountNumber,
                SortCode.defaultSortCode(),
                userId,
                accountName,
                AccountType.PERSONAL
        );

        when(accountRepository.findByAccountNumber(accountNumber)).thenReturn(Optional.of(account));
        when(accountDomainService.canCloseAccount(account)).thenReturn(true);

        // when
        accountService.delete(accountNumber.getValue(), userId.getValue());

        // then
        verify(accountRepository).deleteByAccountNumber(accountNumber);
    }

    @Test
    void shouldThrowExceptionWhenDeletingNonExistentAccount() {
        // given
        AccountNumber accountNumber = AccountNumber.of("01123456");
        when(accountRepository.findByAccountNumber(accountNumber)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> accountService.delete(accountNumber.getValue(), userId.getValue()))
                .isInstanceOf(AccountNotFoundException.class);
    }
}
