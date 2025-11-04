package com.eaglebank.domain.model.account;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Currency;

import static org.assertj.core.api.Assertions.*;

class MoneyTest {

    private static final Currency GBP = Currency.getInstance("GBP");
    private static final Currency USD = Currency.getInstance("USD");

    @Test
    void shouldCreateMoneyWithBigDecimal() {
        // given
        BigDecimal amount = new BigDecimal("100.50");

        // when
        Money money = Money.of(amount, GBP);

        // then
        assertThat(money.getAmount()).isEqualByComparingTo("100.50");
        assertThat(money.getCurrency()).isEqualTo(GBP);
    }

    @Test
    void shouldCreateMoneyWithDouble() {
        // when
        Money money = Money.of(100.50, GBP);

        // then
        assertThat(money.getAmount()).isEqualByComparingTo("100.50");
    }

    @Test
    void shouldCreateGbpMoney() {
        // when
        Money money = Money.gbp(new BigDecimal("50.00"));

        // then
        assertThat(money.getCurrency()).isEqualTo(GBP);
        assertThat(money.getAmount()).isEqualByComparingTo("50.00");
    }

    @Test
    void shouldCreateZeroMoney() {
        // when
        Money money = Money.zero();

        // then
        assertThat(money.getAmount()).isEqualByComparingTo(BigDecimal.ZERO);
        assertThat(money.getCurrency()).isEqualTo(GBP);
    }

    @Test
    void shouldAddMoney() {
        // given
        Money money1 = Money.gbp(100.00);
        Money money2 = Money.gbp(50.00);

        // when
        Money result = money1.add(money2);

        // then
        assertThat(result.getAmount()).isEqualByComparingTo("150.00");
    }

    @Test
    void shouldSubtractMoney() {
        // given
        Money money1 = Money.gbp(100.00);
        Money money2 = Money.gbp(30.00);

        // when
        Money result = money1.subtract(money2);

        // then
        assertThat(result.getAmount()).isEqualByComparingTo("70.00");
    }

    @Test
    void shouldMultiplyMoney() {
        // given
        Money money = Money.gbp(100.00);

        // when
        Money result = money.multiply(new BigDecimal("1.5"));

        // then
        assertThat(result.getAmount()).isEqualByComparingTo("150.00");
    }

    @Test
    void shouldThrowExceptionWhenAddingDifferentCurrencies() {
        // given
        Money gbp = Money.of(100.00, GBP);
        Money usd = Money.of(100.00, USD);

        // when & then
        assertThatThrownBy(() -> gbp.add(usd))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Cannot perform operation on different currencies");
    }

    @Test
    void shouldCompareMoneyAmounts() {
        // given
        Money money1 = Money.gbp(100.00);
        Money money2 = Money.gbp(50.00);
        Money money3 = Money.gbp(100.00);

        // when & then
        assertThat(money1.isGreaterThan(money2)).isTrue();
        assertThat(money2.isLessThan(money1)).isTrue();
        assertThat(money1.isGreaterThanOrEqual(money3)).isTrue();
    }

    @Test
    void shouldCheckIfMoneyIsPositive() {
        // given
        Money positive = Money.gbp(100.00);
        Money zero = Money.zero();
        Money negative = Money.gbp(-50.00);

        // when & then
        assertThat(positive.isPositive()).isTrue();
        assertThat(zero.isPositive()).isFalse();
        assertThat(negative.isPositive()).isFalse();
    }

    @Test
    void shouldCheckIfMoneyIsZero() {
        // given
        Money zero = Money.zero();
        Money nonZero = Money.gbp(0.01);

        // when & then
        assertThat(zero.isZero()).isTrue();
        assertThat(nonZero.isZero()).isFalse();
    }

    @Test
    void shouldCheckIfMoneyIsNegative() {
        // given
        Money negative = Money.gbp(-50.00);
        Money positive = Money.gbp(50.00);

        // when & then
        assertThat(negative.isNegative()).isTrue();
        assertThat(positive.isNegative()).isFalse();
    }

    @Test
    void shouldRoundToTwoDecimalPlaces() {
        // given
        BigDecimal amount = new BigDecimal("100.12345");

        // when
        Money money = Money.of(amount, GBP);

        // then
        assertThat(money.getAmount()).isEqualByComparingTo("100.12");
    }

    @Test
    void shouldHaveValueSemantics() {
        // given
        Money money1 = Money.gbp(100.00);
        Money money2 = Money.gbp(100.00);

        // when & then
        assertThat(money1).isEqualTo(money2);
        assertThat(money1.hashCode()).isEqualTo(money2.hashCode());
    }

    @Test
    void shouldRejectNullAmount() {
        // when & then
        assertThatThrownBy(() -> Money.of(null, GBP))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Amount cannot be null");
    }

    @Test
    void shouldRejectNullCurrency() {
        // when & then
        assertThatThrownBy(() -> Money.of(BigDecimal.TEN, null))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Currency cannot be null");
    }
}
