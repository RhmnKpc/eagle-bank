package com.eaglebank.domain.model.account;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.*;

class AccountNumberTest {

    @ParameterizedTest
    @ValueSource(strings = {"01123456", "01000000", "01999999"})
    void shouldCreateValidAccountNumber(String validAccountNumber) {
        // when
        AccountNumber accountNumber = AccountNumber.of(validAccountNumber);

        // then
        assertThat(accountNumber).isNotNull();
        assertThat(accountNumber.getValue()).isEqualTo(validAccountNumber);
    }

    @Test
    void shouldGenerateValidAccountNumber() {
        // when
        AccountNumber accountNumber = AccountNumber.generate();

        // then
        assertThat(accountNumber).isNotNull();
        assertThat(accountNumber.getValue()).matches("^01\\d{6}$");
    }

    @Test
    void shouldGenerateUniqueAccountNumbers() {
        // when
        AccountNumber accountNumber1 = AccountNumber.generate();
        AccountNumber accountNumber2 = AccountNumber.generate();

        // then
        assertThat(accountNumber1).isNotEqualTo(accountNumber2);
    }

    @ParameterizedTest
    @ValueSource(strings = {"", " ", "  "})
    void shouldRejectBlankAccountNumber(String blank) {
        // when & then
        assertThatThrownBy(() -> AccountNumber.of(blank))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Account number cannot be null or empty");
    }

    @Test
    void shouldRejectNullAccountNumber() {
        // when & then
        assertThatThrownBy(() -> AccountNumber.of(null))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Account number cannot be null or empty");
    }

    @ParameterizedTest
    @ValueSource(strings = {"12345678", "00123456", "011234", "0112345678", "AB123456"})
    void shouldRejectInvalidAccountNumberFormat(String invalid) {
        // when & then
        assertThatThrownBy(() -> AccountNumber.of(invalid))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Invalid account number format");
    }

    @Test
    void shouldHaveValueSemantics() {
        // given
        String accountNum = "01123456";
        AccountNumber accountNumber1 = AccountNumber.of(accountNum);
        AccountNumber accountNumber2 = AccountNumber.of(accountNum);

        // when & then
        assertThat(accountNumber1).isEqualTo(accountNumber2);
        assertThat(accountNumber1.hashCode()).isEqualTo(accountNumber2.hashCode());
    }
}
