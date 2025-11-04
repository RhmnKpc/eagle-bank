package com.eaglebank.domain.model.account;

import com.eaglebank.domain.exception.AccountGenericException;
import com.eaglebank.domain.model.user.UserId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.*;

class AccountTest {

    private AccountNumber accountNumber;
    private SortCode sortCode;
    private UserId ownerId;
    private String accountName;

    @BeforeEach
    void setUp() {
        accountNumber = AccountNumber.of("01123456");
        sortCode = SortCode.defaultSortCode();
        ownerId = UserId.of("usr-123");
        accountName = "My Account";
    }

    @Test
    void shouldCreateNewAccount() {
        // when
        Account account = Account.create(accountNumber, sortCode, ownerId, accountName, AccountType.PERSONAL);

        // then
        assertThat(account).isNotNull();
        assertThat(account.getAccountNumber()).isEqualTo(accountNumber);
        assertThat(account.getSortCode()).isEqualTo(sortCode);
        assertThat(account.getOwnerId()).isEqualTo(ownerId);
        assertThat(account.getName()).isEqualTo(accountName);
        assertThat(account.getType()).isEqualTo(AccountType.PERSONAL);
        assertThat(account.getStatus()).isEqualTo(AccountStatus.ACTIVE);
        assertThat(account.getBalance().isZero()).isTrue();
        assertThat(account.getCreatedAt()).isNotNull();
        assertThat(account.getUpdatedAt()).isNotNull();
    }

    @Test
    void shouldRejectNullAccountNumber() {
        // when & then
        assertThatThrownBy(() -> Account.create(null, sortCode, ownerId, accountName, AccountType.PERSONAL))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Account number cannot be null");
    }

    @Test
    void shouldRejectNullOwnerId() {
        // when & then
        assertThatThrownBy(() -> Account.create(accountNumber, sortCode, null, accountName, AccountType.PERSONAL))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Owner ID cannot be null");
    }

    @Test
    void shouldRejectBlankAccountName() {
        // when & then
        assertThatThrownBy(() -> Account.create(accountNumber, sortCode, ownerId, "", AccountType.PERSONAL))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Account name cannot be null or empty");
    }

    @Test
    void shouldDepositMoney() {
        // given
        Account account = Account.create(accountNumber, sortCode, ownerId, accountName, AccountType.PERSONAL);
        Money depositAmount = Money.gbp(100.00);

        // when
        account.deposit(depositAmount);

        // then
        assertThat(account.getBalance()).isEqualTo(depositAmount);
    }

    @Test
    void shouldRejectDepositToSuspendedAccount() {
        // given
        Account account = Account.create(accountNumber, sortCode, ownerId, accountName, AccountType.PERSONAL);
        account.suspend();

        // when & then
        assertThatThrownBy(() -> account.deposit(Money.gbp(100.00)))
            .isInstanceOf(AccountGenericException.class)
            .hasMessageContaining("Cannot deposit to account with status");
    }

    @Test
    void shouldRejectNegativeDeposit() {
        // given
        Account account = Account.create(accountNumber, sortCode, ownerId, accountName, AccountType.PERSONAL);

        // when & then
        assertThatThrownBy(() -> account.deposit(Money.gbp(-100.00)))
            .isInstanceOf(AccountGenericException.class)
            .hasMessageContaining("Deposit amount must be positive");
    }

    @Test
    void shouldWithdrawMoney() {
        // given
        Account account = Account.create(accountNumber, sortCode, ownerId, accountName, AccountType.PERSONAL);
        account.deposit(Money.gbp(200.00));

        // when
        account.withdraw(Money.gbp(50.00));

        // then
        assertThat(account.getBalance()).isEqualTo(Money.gbp(150.00));
    }

    @Test
    void shouldRejectWithdrawalWithInsufficientFunds() {
        // given
        Account account = Account.create(accountNumber, sortCode, ownerId, accountName, AccountType.PERSONAL);
        account.deposit(Money.gbp(50.00));

        // when & then
        assertThatThrownBy(() -> account.withdraw(Money.gbp(100.00)))
            .isInstanceOf(AccountGenericException.class)
            .hasMessageContaining("Insufficient funds");
    }

    @Test
    void shouldUpdateAccountName() {
        // given
        Account account = Account.create(accountNumber, sortCode, ownerId, accountName, AccountType.PERSONAL);
        String newName = "Updated Account Name";

        // when
        account.updateAccountName(newName);

        // then
        assertThat(account.getName()).isEqualTo(newName);
    }

    @Test
    void shouldCloseAccountWithZeroBalance() {
        // given
        Account account = Account.create(accountNumber, sortCode, ownerId, accountName, AccountType.PERSONAL);

        // when
        account.close();

        // then
        assertThat(account.getStatus()).isEqualTo(AccountStatus.CLOSED);
    }

    @Test
    void shouldRejectClosingAccountWithNonZeroBalance() {
        // given
        Account account = Account.create(accountNumber, sortCode, ownerId, accountName, AccountType.PERSONAL);
        account.deposit(Money.gbp(100.00));

        // when & then
        assertThatThrownBy(() -> account.close())
            .isInstanceOf(AccountGenericException.class)
            .hasMessageContaining("Cannot close account with non-zero balance");
    }

    @Test
    void shouldSuspendAccount() {
        // given
        Account account = Account.create(accountNumber, sortCode, ownerId, accountName, AccountType.PERSONAL);

        // when
        account.suspend();

        // then
        assertThat(account.getStatus()).isEqualTo(AccountStatus.SUSPENDED);
    }

    @Test
    void shouldActivateAccount() {
        // given
        Account account = Account.create(accountNumber, sortCode, ownerId, accountName, AccountType.PERSONAL);
        account.suspend();

        // when
        account.activate();

        // then
        assertThat(account.getStatus()).isEqualTo(AccountStatus.ACTIVE);
    }

    @Test
    void shouldRejectActivatingClosedAccount() {
        // given
        Account account = Account.create(accountNumber, sortCode, ownerId, accountName, AccountType.PERSONAL);
        account.close();

        // when & then
        assertThatThrownBy(() -> account.activate())
            .isInstanceOf(AccountGenericException.class)
            .hasMessageContaining("Cannot activate a closed account");
    }

    @Test
    void shouldCheckOwnership() {
        // given
        Account account = Account.create(accountNumber, sortCode, ownerId, accountName, AccountType.PERSONAL);
        UserId differentUserId = UserId.of("usr-456");

        // when & then
        assertThat(account.isOwnedBy(ownerId)).isTrue();
        assertThat(account.isOwnedBy(differentUserId)).isFalse();
    }

    @Test
    void shouldReconstituteAccount() {
        // given
        Money balance = Money.gbp(500.00);

        // when
        Account account = Account.reconstitute(
            accountNumber, sortCode, ownerId, accountName, AccountType.PERSONAL,
            AccountStatus.ACTIVE, balance, null, null
        );

        // then
        assertThat(account.getBalance()).isEqualTo(balance);
        assertThat(account.getStatus()).isEqualTo(AccountStatus.ACTIVE);
    }
}
