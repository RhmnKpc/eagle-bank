package com.eaglebank.domain.model.user;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.*;

class EmailTest {

    @Test
    void shouldCreateValidEmail() {
        // given
        String validEmail = "user@example.com";

        // when
        Email email = Email.of(validEmail);

        // then
        assertThat(email).isNotNull();
        assertThat(email.getValue()).isEqualTo(validEmail.toLowerCase());
    }

    @Test
    void shouldNormalizeEmailToLowerCase() {
        // given
        String mixedCaseEmail = "User@EXAMPLE.COM";

        // when
        Email email = Email.of(mixedCaseEmail);

        // then
        assertThat(email.getValue()).isEqualTo("user@example.com");
    }

    @ParameterizedTest
    @ValueSource(strings = {"", " ", "  "})
    void shouldRejectBlankEmail(String blankEmail) {
        // when & then
        assertThatThrownBy(() -> Email.of(blankEmail))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Email cannot be null or empty");
    }

    @Test
    void shouldRejectNullEmail() {
        // when & then
        assertThatThrownBy(() -> Email.of(null))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Email cannot be null or empty");
    }

    @ParameterizedTest
    @ValueSource(strings = {"invalid", "@example.com", "user@", "user@.com"})
    void shouldRejectInvalidEmailFormat(String invalidEmail) {
        // when & then
        assertThatThrownBy(() -> Email.of(invalidEmail))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Invalid email format");
    }

    @Test
    void shouldHaveValueSemantics() {
        // given
        Email email1 = Email.of("user@example.com");
        Email email2 = Email.of("USER@example.com");

        // when & then
        assertThat(email1).isEqualTo(email2);
        assertThat(email1.hashCode()).isEqualTo(email2.hashCode());
    }

    @Test
    void shouldReturnValueAsString() {
        // given
        Email email = Email.of("user@example.com");

        // when & then
        assertThat(email.getValue()).isEqualTo("user@example.com");
    }
}
