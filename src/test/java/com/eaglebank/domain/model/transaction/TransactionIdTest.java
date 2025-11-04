package com.eaglebank.domain.model.transaction;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.*;

class TransactionIdTest {

    @Test
    void shouldGenerateTransactionId() {
        // when
        TransactionId transactionId = TransactionId.generate();

        // then
        assertThat(transactionId).isNotNull();
        assertThat(transactionId.getValue()).startsWith("tan-");
        assertThat(transactionId.getValue().length()).isGreaterThan(4);
    }

    @Test
    void shouldGenerateUniqueTransactionIds() {
        // when
        TransactionId id1 = TransactionId.generate();
        TransactionId id2 = TransactionId.generate();

        // then
        assertThat(id1).isNotEqualTo(id2);
        assertThat(id1.getValue()).isNotEqualTo(id2.getValue());
    }

    @Test
    void shouldCreateTransactionIdFromValidString() {
        // given
        String validId = "tan-123e4567-e89b-12d3-a456-426614174000";

        // when
        TransactionId transactionId = TransactionId.of(validId);

        // then
        assertThat(transactionId).isNotNull();
        assertThat(transactionId.getValue()).isEqualTo(validId);
    }

    @ParameterizedTest
    @ValueSource(strings = {"", " ", "  "})
    void shouldRejectBlankTransactionId(String blankId) {
        // when & then
        assertThatThrownBy(() -> TransactionId.of(blankId))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("TransactionId cannot be null or empty");
    }

    @Test
    void shouldRejectNullTransactionId() {
        // when & then
        assertThatThrownBy(() -> TransactionId.of(null))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("TransactionId cannot be null or empty");
    }

    @Test
    void shouldHaveValueSemantics() {
        // given
        String idValue = "tan-123e4567-e89b-12d3-a456-426614174000";
        TransactionId id1 = TransactionId.of(idValue);
        TransactionId id2 = TransactionId.of(idValue);

        // when & then
        assertThat(id1).isEqualTo(id2);
        assertThat(id1.hashCode()).isEqualTo(id2.hashCode());
    }
}
