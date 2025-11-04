package com.eaglebank.domain.model.transaction;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.*;

class TransactionReferenceTest {

    @Test
    void shouldCreateValidTransactionReference() {
        // given
        String validReference = "REF-12345";

        // when
        TransactionReference reference = TransactionReference.of(validReference);

        // then
        assertThat(reference).isNotNull();
        assertThat(reference.getValue()).isEqualTo(validReference);
    }

    @Test
    void shouldCreateTransactionReferenceWithMaxLength() {
        // given
        String maxLengthReference = "A".repeat(100);

        // when
        TransactionReference reference = TransactionReference.of(maxLengthReference);

        // then
        assertThat(reference).isNotNull();
        assertThat(reference.getValue()).hasSize(100);
    }

    @ParameterizedTest
    @ValueSource(strings = {"", " ", "  "})
    void shouldRejectBlankTransactionReference(String blankReference) {
        // when & then
        assertThatThrownBy(() -> TransactionReference.of(blankReference))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Transaction reference cannot be null or empty");
    }

    @Test
    void shouldRejectNullTransactionReference() {
        // when & then
        assertThatThrownBy(() -> TransactionReference.of(null))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Transaction reference cannot be null or empty");
    }

    @Test
    void shouldRejectTransactionReferenceExceeding100Characters() {
        // given
        String tooLongReference = "A".repeat(101);

        // when & then
        assertThatThrownBy(() -> TransactionReference.of(tooLongReference))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Transaction reference cannot exceed 100 characters");
    }

    @Test
    void shouldHaveValueSemantics() {
        // given
        String refValue = "REF-12345";
        TransactionReference ref1 = TransactionReference.of(refValue);
        TransactionReference ref2 = TransactionReference.of(refValue);

        // when & then
        assertThat(ref1).isEqualTo(ref2);
        assertThat(ref1.hashCode()).isEqualTo(ref2.hashCode());
    }
}
