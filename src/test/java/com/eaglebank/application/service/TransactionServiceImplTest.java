package com.eaglebank.application.service;

import com.eaglebank.application.transaction.TransactionService;
import com.eaglebank.application.transaction.TransactionServiceImpl;
import com.eaglebank.domain.exception.AccountNotFoundException;
import com.eaglebank.domain.exception.TransactionNotFoundException;
import com.eaglebank.domain.exception.UnauthorizedAccessException;
import com.eaglebank.domain.model.account.*;
import com.eaglebank.domain.model.transaction.Transaction;
import com.eaglebank.domain.model.transaction.TransactionId;
import com.eaglebank.domain.model.transaction.TransactionReference;
import com.eaglebank.domain.model.transaction.TransactionType;
import com.eaglebank.domain.model.user.UserId;
import com.eaglebank.domain.repository.AccountRepository;
import com.eaglebank.domain.repository.TransactionRepository;
import com.eaglebank.domain.service.TransactionDomainService;
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
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TransactionServiceImplTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private TransactionDomainService transactionDomainService;

    @InjectMocks
    private TransactionServiceImpl transactionService;

    private Account testAccount;
    private AccountNumber accountNumber;
    private UserId ownerId;
    private Transaction testTransaction;
    private TransactionId transactionId;

    @BeforeEach
    void setUp() {
        accountNumber = AccountNumber.of("01336459");
        ownerId = UserId.generate();
        testAccount = Account.create(
                accountNumber,
                SortCode.of("12-34-56"),
                ownerId,
                "Test Account",
                AccountType.PERSONAL
        );

        transactionId = TransactionId.generate();
        testTransaction = Transaction.create(
                transactionId,
                accountNumber,
                TransactionType.DEPOSIT,
                Money.gbp(100.00),
                Money.gbp(100.00),
                TransactionReference.of("REF-12345")
        );
    }

    @Test
    void shouldCreateDepositTransaction() {
        // given
        when(accountRepository.findByAccountNumber(accountNumber)).thenReturn(Optional.of(testAccount));
        when(transactionDomainService.createDepositTransaction(any(), any(), any()))
                .thenReturn(testTransaction);
        when(transactionRepository.save(any(Transaction.class))).thenReturn(testTransaction);

        TransactionService.CreateTransactionCommand command = new TransactionService.CreateTransactionCommand(
                accountNumber.getValue(),
                ownerId.getValue(),
                "DEPOSIT",
                100.00,
                "REF-12345");

        // when
        Transaction result = transactionService.create(command);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getType()).isEqualTo(TransactionType.DEPOSIT);
        verify(accountRepository).findByAccountNumber(accountNumber);
        verify(transactionDomainService).createDepositTransaction(any(), any(), any());
        verify(transactionRepository).save(any(Transaction.class));
    }

    @Test
    void shouldCreateWithdrawalTransaction() {
        // given
        testAccount.deposit(Money.gbp(200.00)); // Ensure sufficient balance
        when(accountRepository.findByAccountNumber(accountNumber)).thenReturn(Optional.of(testAccount));

        Transaction withdrawalTransaction = Transaction.create(
                transactionId,
                accountNumber,
                TransactionType.WITHDRAWAL,
                Money.gbp(50.00),
                Money.gbp(150.00),
                TransactionReference.of("REF-67890"));

        when(transactionDomainService.createWithdrawalTransaction(any(), any(), any()))
                .thenReturn(withdrawalTransaction);
        when(transactionRepository.save(any(Transaction.class))).thenReturn(withdrawalTransaction);

        TransactionService.CreateTransactionCommand command = new TransactionService.CreateTransactionCommand(
                accountNumber.getValue(),
                ownerId.getValue(),
                "WITHDRAWAL",
                50.00,
                "REF-67890"
        );

        // when
        Transaction result = transactionService.create(command);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getType()).isEqualTo(TransactionType.WITHDRAWAL);
        verify(transactionDomainService).createWithdrawalTransaction(any(), any(), any());
    }

    @Test
    void shouldThrowExceptionWhenAccountNotFoundOnCreate() {
        // given
        when(accountRepository.findByAccountNumber(accountNumber)).thenReturn(Optional.empty());

        TransactionService.CreateTransactionCommand command = new TransactionService.CreateTransactionCommand(
                accountNumber.getValue(),
                ownerId.toString(),
                "DEPOSIT",
                100.00,
                "REF-12345"
        );

        // when & then
        assertThatThrownBy(() -> transactionService.create(command))
                .isInstanceOf(AccountNotFoundException.class);
    }

    @Test
    void shouldThrowExceptionWhenUserNotAuthorizedOnCreate() {
        // given
        UserId unauthorizedUser = UserId.generate();
        when(accountRepository.findByAccountNumber(accountNumber)).thenReturn(Optional.of(testAccount));

        TransactionService.CreateTransactionCommand command = new TransactionService.CreateTransactionCommand(
                accountNumber.getValue(),
                unauthorizedUser.getValue(),
                "DEPOSIT",
                100.00,
                "REF-12345"
        );

        // when & then
        assertThatThrownBy(() -> transactionService.create(command))
                .isInstanceOf(UnauthorizedAccessException.class);
    }

    @Test
    void shouldThrowExceptionForInvalidTransactionType() {
        // given
        when(accountRepository.findByAccountNumber(accountNumber)).thenReturn(Optional.of(testAccount));

        TransactionService.CreateTransactionCommand command = new TransactionService.CreateTransactionCommand(
                accountNumber.getValue(),
                ownerId.getValue(),
                "INVALID_TYPE",
                100.00,
                "REF-12345"
        );

        // when & then
        assertThatThrownBy(() -> transactionService.create(command))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Invalid transaction type");
    }

    @Test
    void shouldGetTransactionById() {
        // given
        when(accountRepository.findByAccountNumber(accountNumber)).thenReturn(Optional.of(testAccount));
        when(transactionRepository.findById(transactionId)).thenReturn(Optional.of(testTransaction));

        // when
        Transaction result = transactionService.get(
                accountNumber.getValue(),
                transactionId.getValue(),
                ownerId.getValue()
        );

        // then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(transactionId);
        verify(accountRepository).findByAccountNumber(accountNumber);
        verify(transactionRepository).findById(transactionId);
    }

    @Test
    void shouldThrowExceptionWhenAccountNotFoundOnGet() {
        // given
        when(accountRepository.findByAccountNumber(accountNumber)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> transactionService.get(
                accountNumber.getValue(),
                transactionId.getValue(),
                ownerId.getValue()
        ))
                .isInstanceOf(AccountNotFoundException.class);
    }

    @Test
    void shouldThrowExceptionWhenUserNotAuthorizedOnGet() {
        // given
        UserId unauthorizedUser = UserId.generate();
        when(accountRepository.findByAccountNumber(accountNumber)).thenReturn(Optional.of(testAccount));

        // when & then
        assertThatThrownBy(() -> transactionService.get(
                accountNumber.getValue(),
                transactionId.getValue(),
                unauthorizedUser.getValue()
        ))
                .isInstanceOf(UnauthorizedAccessException.class);
    }

    @Test
    void shouldThrowExceptionWhenTransactionNotFound() {
        // given
        when(accountRepository.findByAccountNumber(accountNumber)).thenReturn(Optional.of(testAccount));
        when(transactionRepository.findById(transactionId)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> transactionService.get(
                accountNumber.getValue(),
                transactionId.getValue(),
                ownerId.getValue()
        ))
                .isInstanceOf(TransactionNotFoundException.class);
    }

    @Test
    void shouldThrowExceptionWhenTransactionBelongsToDifferentAccount() {
        // given
        AccountNumber differentAccountNumber = AccountNumber.of("01336455");
        Transaction differentAccountTransaction = Transaction.create(
                transactionId,
                differentAccountNumber,
                TransactionType.DEPOSIT,
                Money.gbp(100.00),
                Money.gbp(100.00),
                TransactionReference.of("REF-12345")
        );

        when(accountRepository.findByAccountNumber(accountNumber)).thenReturn(Optional.of(testAccount));
        when(transactionRepository.findById(transactionId)).thenReturn(Optional.of(differentAccountTransaction));

        // when & then
        assertThatThrownBy(() -> transactionService.get(
                accountNumber.getValue(),
                transactionId.getValue(),
                ownerId.getValue()
        ))
                .isInstanceOf(TransactionNotFoundException.class)
                .hasMessageContaining("does not belong to account");
    }

    @Test
    void shouldListTransactionsForAccount() {
        // given
        List<Transaction> transactions = List.of(testTransaction);
        when(accountRepository.findByAccountNumber(accountNumber)).thenReturn(Optional.of(testAccount));
        when(transactionRepository.findByAccountNumber(accountNumber)).thenReturn(transactions);

        // when
        List<Transaction> result = transactionService.list(accountNumber.getValue(), ownerId.getValue());

        // then
        assertThat(result).hasSize(1);
        assertThat(result).contains(testTransaction);
        verify(accountRepository).findByAccountNumber(accountNumber);
        verify(transactionRepository).findByAccountNumber(accountNumber);
    }

    @Test
    void shouldThrowExceptionWhenAccountNotFoundOnList() {
        // given
        when(accountRepository.findByAccountNumber(accountNumber)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> transactionService.list(
                accountNumber.getValue(),
                ownerId.getValue()
        ))
                .isInstanceOf(AccountNotFoundException.class);
    }

    @Test
    void shouldThrowExceptionWhenUserNotAuthorizedOnList() {
        // given
        UserId unauthorizedUser = UserId.generate();
        when(accountRepository.findByAccountNumber(accountNumber)).thenReturn(Optional.of(testAccount));

        // when & then
        assertThatThrownBy(() -> transactionService.list(
                accountNumber.getValue(),
                unauthorizedUser.getValue()
        ))
                .isInstanceOf(UnauthorizedAccessException.class);
    }
}
