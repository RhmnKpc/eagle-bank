package com.eaglebank.domain.model.transaction;

import com.eaglebank.domain.model.account.AccountNumber;
import com.eaglebank.domain.model.account.Money;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class TransactionTest {

    private TransactionId transactionId;
    private AccountNumber accountNumber;
    private Money amount;
    private Money balanceAfter;
    private TransactionReference reference;

    @BeforeEach
    void setUp() {
        transactionId = TransactionId.generate();
        accountNumber = AccountNumber.of("01336459");
        amount = Money.gbp(100.00);
        balanceAfter = Money.gbp(500.00);
        reference = TransactionReference.of("REF-12345");
    }

    @Test
    void shouldCreateDepositTransaction() {
        // when
        Transaction transaction = Transaction.create(
                transactionId, accountNumber, TransactionType.DEPOSIT,
                amount, balanceAfter, reference
        );

        // then
        assertThat(transaction).isNotNull();
        assertThat(transaction.getId()).isEqualTo(transactionId);
        assertThat(transaction.getAccountNumber()).isEqualTo(accountNumber);
        assertThat(transaction.getType()).isEqualTo(TransactionType.DEPOSIT);
        assertThat(transaction.getAmount()).isEqualTo(amount);
        assertThat(transaction.getBalanceAfter()).isEqualTo(balanceAfter);
        assertThat(transaction.getReference()).isEqualTo(reference);
        assertThat(transaction.getCreatedAt()).isNotNull();
        assertThat(transaction.isCredit()).isTrue();
        assertThat(transaction.isDebit()).isFalse();
    }

    @Test
    void shouldCreateWithdrawalTransaction() {
        // when
        Transaction transaction = Transaction.create(
                transactionId, accountNumber, TransactionType.WITHDRAWAL,
                amount, balanceAfter, reference
        );

        // then
        assertThat(transaction).isNotNull();
        assertThat(transaction.getType()).isEqualTo(TransactionType.WITHDRAWAL);
        assertThat(transaction.isCredit()).isFalse();
        assertThat(transaction.isDebit()).isTrue();
    }

    @Test
    void shouldReconstituteTransactionWithTimestamp() {
        // given
        LocalDateTime pastTimestamp = LocalDateTime.now().minusDays(1);

        // when
        Transaction transaction = Transaction.reconstitute(
                transactionId, accountNumber, TransactionType.DEPOSIT,
                amount, balanceAfter, reference, pastTimestamp
        );

        // then
        assertThat(transaction).isNotNull();
        assertThat(transaction.getCreatedAt()).isEqualTo(pastTimestamp);
    }

    @Test
    void shouldRejectNullTransactionId() {
        // when & then
        assertThatThrownBy(() -> Transaction.create(
                null, accountNumber, TransactionType.DEPOSIT,
                amount, balanceAfter, reference
        ))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Transaction ID cannot be null");
    }

    @Test
    void shouldRejectNullAccountNumber() {
        // when & then
        assertThatThrownBy(() -> Transaction.create(
                transactionId, null, TransactionType.DEPOSIT,
                amount, balanceAfter, reference
        ))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Account number cannot be null");
    }

    @Test
    void shouldRejectNullTransactionType() {
        // when & then
        assertThatThrownBy(() -> Transaction.create(
                transactionId, accountNumber, null,
                amount, balanceAfter, reference
        ))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Transaction type cannot be null");
    }

    @Test
    void shouldRejectNullAmount() {
        // when & then
        assertThatThrownBy(() -> Transaction.create(
                transactionId, accountNumber, TransactionType.DEPOSIT,
                null, balanceAfter, reference
        ))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Amount cannot be null");
    }

    @Test
    void shouldRejectZeroAmount() {
        // when & then
        assertThatThrownBy(() -> Transaction.create(
                transactionId, accountNumber, TransactionType.DEPOSIT,
                Money.gbp(0.00), balanceAfter, reference
        ))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Transaction amount must be positive");
    }

    @Test
    void shouldRejectNegativeAmount() {
        // when & then
        assertThatThrownBy(() -> Transaction.create(
                transactionId, accountNumber, TransactionType.DEPOSIT,
                Money.gbp(-50.00), balanceAfter, reference
        ))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Transaction amount must be positive");
    }

    @Test
    void shouldRejectNullBalanceAfter() {
        // when & then
        assertThatThrownBy(() -> Transaction.create(
                transactionId, accountNumber, TransactionType.DEPOSIT,
                amount, null, reference
        ))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Balance after cannot be null");
    }

    @Test
    void shouldRejectNullReference() {
        // when & then
        assertThatThrownBy(() -> Transaction.create(
                transactionId, accountNumber, TransactionType.DEPOSIT,
                amount, balanceAfter, null
        ))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Transaction reference cannot be null");
    }

    @Test
    void shouldHaveIdentityBasedOnId() {
        // given
        Transaction transaction1 = Transaction.create(
                transactionId, accountNumber, TransactionType.DEPOSIT,
                amount, balanceAfter, reference
        );
        Transaction transaction2 = Transaction.create(
                transactionId, accountNumber, TransactionType.WITHDRAWAL,
                Money.gbp(50.00), Money.gbp(450.00), reference
        );

        // when & then
        assertThat(transaction1).isEqualTo(transaction2);
        assertThat(transaction1.hashCode()).isEqualTo(transaction2.hashCode());
    }

    @Test
    void shouldNotBeEqualWithDifferentIds() {
        // given
        Transaction transaction1 = Transaction.create(
                transactionId, accountNumber, TransactionType.DEPOSIT,
                amount, balanceAfter, reference
        );
        Transaction transaction2 = Transaction.create(
                TransactionId.generate(), accountNumber, TransactionType.DEPOSIT,
                amount, balanceAfter, reference
        );

        // when & then
        assertThat(transaction1).isNotEqualTo(transaction2);
    }
}
